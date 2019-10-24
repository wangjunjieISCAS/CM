package com.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import com.data.Constants;
import com.data.TestProject;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;

public class RandOrderProjectsEvaluation {

	public RandOrderProjectsEvaluation() {
		// TODO Auto-generated constructor stub
	}
	
	public void obtainPerformanceUnderOptimalParameterValues ( String folderName, String outFileName ) {
		PerformanceReader perfReader = new PerformanceReader();
		TuneOptimalParameters tuneTool = new TuneOptimalParameters();
		
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectList( Constants.projectFolder );
		projectList = ProjectRankTimeSeries.randomRankProjectList(projectList);
				
		TreeMap<Integer, Double[]> performanceList = new TreeMap<Integer, Double[]>();
		TreeMap<Integer, String> bestParaList = new TreeMap<Integer, String>();
		for ( int i = Constants.EVALUATION_TIME_SERIES_BEGIN; i < projectList.size(); i++ ) {
			//train set is projectList(0) - projectList(beginTaskId-1)
			String formerProjectName = projectList.get(i-1).getProjectName();
			String[] temp = formerProjectName.split("-");
			Integer formerProjectId = Integer.parseInt( temp[0]);
			String bestPara = tuneTool.computeBestParameterForTaskSubset(folderName, formerProjectId );
			
			String curProjectName = projectList.get(i).getProjectName();
			String[] curTemp = curProjectName.split("-");
			Integer curProjectId = Integer.parseInt( curTemp[0]);
			Double[] performance = perfReader.readPerformanceSpecificParameterSpecificProject(folderName, Constants.attrName, bestPara, curProjectId );
			
			performanceList.put( curProjectId, performance );
			bestParaList.put( curProjectId, bestPara );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File( outFileName )));
			for ( Integer projectId : performanceList.keySet() ) {
				writer.write( projectId + "," );
				Double[] performance = performanceList.get( projectId );
				for ( int i=0; i < performance.length; i++  ) {
					writer.write( performance[i] + ",");
				}
				writer.write( "," + "," + bestParaList.get( projectId ) );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void obtainStatisticdAllValidations ( String folderName ){
		HashMap<Integer, ArrayList<Double[]>> performanceList = new HashMap<Integer, ArrayList<Double[]>>();
		
		PerformanceReader reader = new PerformanceReader();
		File validationFolder = new File ( folderName );
		if ( validationFolder.isDirectory() ) {
			String[] validationFileList = validationFolder.list();
			for ( int i =0; i < validationFileList.length; i++ ) {
				String fileName = validationFileList[i];
				
				HashMap<Integer, Double[]> performance = reader.readPerformanceNoHeaderOnlyPID( folderName + "/" + fileName );
				for ( Integer projectId : performance.keySet() ){
					ArrayList<Double[]> values = new ArrayList<Double[]>();
					if ( performanceList.containsKey( projectId )){
						values = performanceList.get( projectId );
					}
					values.add( performance.get( projectId ));
					performanceList.put( projectId, values );
				}
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File( "data/output/performanceLightPred/randOrderPerformance.csv" )));
			
			for ( Integer projectId: performanceList.keySet() ){
				System.out.println( projectId + ": " + performanceList.get( projectId).size() );
				
				writer.write( projectId + ",");
				ArrayList<Double[]> performance = performanceList.get( projectId );
				for ( int i =0; i < Constants.attrName.length; i++ ){
					Double[] statis = this.computeStatistics( performance, i);
					
					for ( int j =0; j < statis.length; j++ ){
						writer.write( statis[j] + ",");
					}
					writer.write( " " + "," );
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
	
	
	public Double[] computeStatistics ( ArrayList<Double[]> performanceList, int index ){
		ArrayList<Double> values = new ArrayList<Double>();
		for ( int i=0; i < performanceList.size(); i++ ){
			values.add( performanceList.get( i)[index]);
		}
		
		Collections.sort( values );
		Integer medianIndex  = values.size() / 2;
		Integer oneQuartIndex = values.size() / 4;
		Integer thirdQuartIndex = (values.size() / 4 )*3;
		
		Double min = values.get( 0 );
		Double oneQuart = values.get( oneQuartIndex );
		Double median = values.get( medianIndex );
		Double thirdQuart = values.get( thirdQuartIndex );
		Double max = values.get( values.size()-1 );
		
		Double[] statis = { min, oneQuart, median, thirdQuart, max };
		return statis;
	}
	
	public static void main ( String[] args ){
		RandOrderProjectsEvaluation evaluation = new RandOrderProjectsEvaluation();
		/*
		String folderName = "data/output/performanceLightPred/tune-sub";
		for ( int i =0; i < 1000; i++ ){
			evaluation.obtainPerformanceUnderOptimalParameterValues(folderName, "data/output/performanceLightPred/randOrder/performance-cmb-" + i + ".csv");
		}
		*/
		evaluation.obtainStatisticdAllValidations( "data/output/performanceLightPred/randOrder");
	}
}
