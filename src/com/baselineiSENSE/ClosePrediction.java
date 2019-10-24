package com.baselineiSENSE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.data.Constants;
import com.data.TestProject;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;

public class ClosePrediction {
	public Double[] predictCloseTime ( TestProject project, Integer[] thresList ) {
		Double[] performance = null;
		return performance;
	}
	
	
	public void predictCloseTimeForProjects ( String folderName, String performanceFile, Integer[] thresList) {
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectAndTaskList(Constants.projectFolder, Constants.taskFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( performanceFile )));
			writer.write( "project" + "," + "bugsDetected" + "," + "totalBugs" + "," + "percentBugsDetected" + "," + "reportsSubmit" + "," + "totalReports" + "," 
					+ "percentReportsSubmit" + "," + "percentSavedEffort" + "," + "F1" + "," + "E1" + 
					"," + "optimalReportsSubmit" + "," + "differenceWithOptimal");
			writer.newLine();
			
			for ( int i = 0; i < projectList.size(); i++ ){
				TestProject project = projectList.get(i);
				Double[] performance = this.predictCloseTime(project, thresList);
				
				writer.write( project.getProjectName() + ",");
				for ( int j =0;  j< performance.length; j++ ) {
					writer.write( performance[j] + ",");
				}
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//后来改成了每个方法只有一个参数，所以这里的candThres是所有参数的集合；而上面方法的thresList是方法的参数（因为之前有方法是两个参数）
	public void crossValidationForProjects ( String folderName, Integer[] candPara, String outFolderName, int repeatTime ) {
		ArrayList<TestProject> projectList = new ArrayList<TestProject>();
		
		TestProjectReader reader = new TestProjectReader();
		File projectsFolder = new File ( folderName );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String fileName = folderName + "/" + projectFileList[i];
				
				TestProject project = reader.loadTestProject( fileName );
				projectList.add( project );
			}
		}
		
		//<project, <para, value>>
		HashMap<String, HashMap<Integer, Double[]>> storedPerformance = new HashMap<String, HashMap<Integer, Double[]>>();
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			System.out.println( "Calculating the performance of " + project.getProjectName() );
			
			HashMap<Integer, Double[]> performance = new HashMap<Integer, Double[]>();
			for ( int j =0; j < candPara.length; j++ ) {
				Integer[] thresList = { candPara[j], 1};
				Double[] perf = this.predictCloseTime(project, thresList);
				
				performance.put( candPara[j], perf );
			}
			storedPerformance.put( project.getProjectName(), performance );
		}
		System.out.println( "Done performance calculating!");
		
		HashMap<Integer, Integer> optParaTime = new HashMap<Integer, Integer>();
		
		//cross validation repeat times
		for ( int k =0; k < repeatTime; k++ ) {
			HashMap<Integer, ArrayList<String>> projectFoldList = this.separateIntoFolds(projectList);
			HashMap<String, Double[]> bestPerformance = new HashMap<String, Double[]>();
			for ( Integer fold : projectFoldList.keySet() ) {
				ArrayList<String> testSet = projectFoldList.get( fold );
				
				ArrayList<String> trainSet = new ArrayList<String>();
				for ( Integer inFold : projectFoldList.keySet() ) {
					if ( inFold != fold ) {
						trainSet.addAll( projectFoldList.get( inFold ));
					}
				}
				
				Integer optPara = this.tuneParameters(trainSet, storedPerformance, candPara);
				int count = 1;
				if ( optParaTime.containsKey( optPara)) {
					count += optParaTime.get( optPara);
				}
				optParaTime.put( optPara, count );

				//use the optimal parameter to obtain the performance of testSet
				for ( int i =0; i < testSet.size(); i++ ) {
					String projectName = testSet.get( i );
					
					HashMap<Integer, Double[]> performance = storedPerformance.get( projectName );
					Double[] perf = performance.get( optPara );
					bestPerformance.put( projectName , perf );
				}			
			}
			//System.out.println ( "bestPerformance size: " + bestPerformance.size() );
			
			//store best performance 
			try {
				BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File ( outFolderName + "/" + k + "-performance"  + ".csv")));
				writer.write( "project" + "," + "bugsDetected" + "," + "totalBugs" + "," + "percentBugsDetected" + "," + "reportsSubmit" + "," + "totalReports" + "," 
						+ "percentReportsSubmit" + "," + "percentSavedEffort" + "," + "F1" + "," + "E1" + 
						"," + "optimalReportsSubmit" + "," + "differenceWithOptimal");
				writer.newLine();
				
				for ( String projectName : bestPerformance.keySet() ) {
					Double[] perf = bestPerformance.get( projectName );
					writer.write( projectName + ",");
					for ( int i =0; i < perf.length; i++ ) {
						writer.write( perf[i] + ",");
					}
					writer.newLine();
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}	
		
		//store to a optimal parameter file
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File ( outFolderName + "/" + "parameter.csv") ));
			
			for ( Integer para : optParaTime.keySet() ) {
				writer.write( para + "," + optParaTime.get( para) );
				writer.newLine();
			}			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	public Integer tuneParameters ( ArrayList<String> trainSet, HashMap<String, HashMap<Integer, Double[]>> storedPerformance, Integer[] candPara ) {
		//para, [3] [train size]
		HashMap<Integer, Double[][]> performance = new HashMap<Integer, Double[][]>();
		for ( int i =0; i < candPara.length; i++ ) {
			Double[][] perf = new Double[3][trainSet.size()];
			performance.put( candPara[i], perf );
		}
		
		for ( int i =0; i < trainSet.size(); i++ ) {
			String projectName = trainSet.get( i );
			HashMap<Integer, Double[]> perf = storedPerformance.get( projectName );
			for ( Integer para : perf.keySet() ) {
				Double[] value = perf.get( para);
				
				Double[][] tPerf = performance.get( para );
				tPerf[0][i] = value[2];
				tPerf[1][i] = value[6];
				tPerf[2][i] = value[7];
				performance.put( para, tPerf );
			}
		}
		
		
		HashMap<Integer, Double[]> paraPerf = new HashMap<Integer, Double[]>();
		for ( Integer para : performance.keySet() ) {
			Double[][] perf = performance.get( para );
			Double[] bugs = perf[0];
			Double[] reports = perf[1];
			Double[] F1 = perf[2];
			
			Double medianBug = this.obtainMedian( bugs );
			Double medianReport = this.obtainMedian( reports );
			Double medianF1 = this.obtainMedian( F1 );
			
			Double[] median = {medianBug, medianReport, medianF1 };
			paraPerf.put( para, median );
		}
		
		Integer optPara = this.findMaxF1( paraPerf );
		System.out.println( optPara );
		
		return optPara;		
	}
	
	//bugs > 0.9, report > 0.3, maximize F1
	public Integer findMaxF1 ( HashMap<Integer, Double[]> performance ) {
		double maxF1 = -1.0;
		int optPara = -1;
		for ( Integer para : performance.keySet() ) {
			Double[] value = performance.get( para );
			if ( value[0] >= 0.9 && value[1] >= 0.3 ) {
				if ( value[2] > maxF1 ) {
					maxF1 = value[2];
					optPara = para;
				}
			}
		}
		
		//没有满足bugs>0.9, report>0.3的限制
		if ( maxF1 == -1.0 ) {
			for ( Integer para : performance.keySet() ) {
				Double[] value = performance.get( para );
				if ( value[2] > maxF1 ) {
					maxF1 = value[2];
					optPara = para;
				}
			}
		}
		return optPara;
	}	
		
	public Double obtainMedian ( Double[] oriValue ) {
		ArrayList<Double> value = new ArrayList<Double>();
		for ( int i =0; i < oriValue.length; i++ )
			value.add( oriValue[i] );
		Collections.sort( value );
		
		Double median = -1.0;
		
		int size = value.size();
		if ( size % 2 == 0 ) {
			median = ( value.get( size / 2) + value.get( size/2-1)) / 2;
		}else {
			median = value.get( size /2 );
		}
		return median;
	}
	
	public HashMap<Integer, ArrayList<String>> separateIntoFolds ( ArrayList<TestProject> projectList ) {
		int foldSize = 3;
		Random rand = new Random();
		HashMap<Integer, ArrayList<String>> projectFoldList = new HashMap<Integer, ArrayList<String>>();
		for ( int i =0; i < foldSize; i++ ) {
			ArrayList<String> projects = new ArrayList<String>();
			projectFoldList.put( i, projects );
		}
		
		int size = projectList.size() / foldSize + 1;
		int maxTry = 10000;
		int i = 0, tryNum = 0;
		while ( i < projectList.size() && tryNum < maxTry ) {
			tryNum ++;
			int index = rand.nextInt( foldSize );
			ArrayList<String> projects = projectFoldList.get( index );
			if ( projects.size() > size ) {
				continue;
			}
			projects.add( projectList.get( i ).getProjectName() );
			
			projectFoldList.put( index, projects );
			i++;
		}
		int index = foldSize -1;
		ArrayList<String> projects = projectFoldList.get( index );
		while ( i < projectList.size() ) {
			projects.add( projectList.get( i).getProjectName() );
			i++;
		}
		projectFoldList.put ( index, projects );
		
		//for test
		/*
		for ( Integer fold : projectFoldList.keySet() ) {
			System.out.println( fold + " " + projectFoldList.get( fold ).size() );
		}
		*/
		return projectFoldList;
	}
	
	
}
