package com.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.RBPredictionDynamic.RBBasePrediction;
import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;

public class DuplicateTagEvaluation {
	HashMap<Integer, Double> optSimThresList = new HashMap<Integer, Double>();
	
	public DuplicateTagEvaluation() {
		// TODO Auto-generated constructor stub
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( "data/input/bestPara-MhCH.csv" )));	
			String line = "";
			while ( (line = br.readLine()) != null ){
				String[] temp = line.split( ",");
				Integer projectId = Integer.parseInt( temp[0]);

				Double simThres = Double.parseDouble( temp[2] );
				optSimThresList.put( projectId, simThres );
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void generatePredictedDuplicateTag ( TestProject project ){
		RBBasePrediction predictionTool = new RBBasePrediction();
		
		ArrayList<Integer> bugTagList = new ArrayList<Integer>();
		ArrayList<Integer> reportPredDupTag = new ArrayList<Integer>();
		ArrayList<HashMap<String, Integer>> histReportTermsList = new ArrayList<HashMap<String, Integer>>();
		
		String[] temp = project.getProjectName().split("-");
		Integer projectId = Integer.parseInt( temp[0] );
		Double simThres = optSimThresList.get( projectId );
		
		Integer curGroupNum = 0;
		for ( int i=0 ; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			HashMap<String, Integer> curReportTerms = WordSegment.obtainUniqueTermForReport( report );
			
			if ( curReportTerms.size() <= 1 ){     //该规则作为是否为bug的判断
				bugTagList.add( 0);
				reportPredDupTag.add( -2 );
				continue;
			}
			bugTagList.add( 1 );
			
			Integer dupTag = predictionTool.obtainDupTagInfo(curReportTerms, histReportTermsList, reportPredDupTag, simThres);
			if ( dupTag == -1 ) {
				dupTag = ++ curGroupNum;
			}
			reportPredDupTag.add( dupTag );
			histReportTermsList.add( curReportTerms );
		}	
		
		this.outputPredictionDupTag(project, bugTagList, reportPredDupTag, "data/output/projectsWithPredictedDupTag/dupTag-" + project.getProjectName() );
	}
	
	public void outputPredictionDupTag ( TestProject project, ArrayList<Integer> bugTagList, ArrayList<Integer> dupTagList, String outputFile ){
		String[] titles = { "case编号", "用户id", "case标题", "地域", "机型", "操作系统", "网络环境", "运营商", "ROM信息", "bug详情",
				"复现步骤", "截图", "是否未知", "优先级", "审核状态", "提交时间", " ", "重复情况" };
		
		ArrayList<String[]> projectDetails = new ArrayList<String[]>();
		projectDetails.add( titles );
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ){
			TestReport report = project.getTestReportsInProj().get( i );
			
			String[] content = new String[titles.length];
			for ( int j =0; j < content.length; j++ ){
				content[j] = " ";
			}
			
			content[0] = ((Integer)report.getTestCaseId()).toString();
			content[1] = report.getUserId();
			content[2] = report.getTestCaseName();
			content[9] = report.getBugDetail().replaceAll(",", ".");
			content[10] = report.getReproSteps().replaceAll(",", ".");
			
			String bugTag = "审核通过";
			if ( bugTagList.get( i) !=1 ){
				bugTag = "审核不通过";
			}
			content[14] = bugTag;
			
			content[15] = "2000/01/01 00:00";
			String dupTag = dupTagList.get( i).toString();
			content[17] = dupTag;
			
			projectDetails.add( content );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter (new File ( outputFile )));
		
			for ( int i =0; i < projectDetails.size(); i++ ){
				String[] details = projectDetails.get( i );
				for ( int j =0; j < details.length; j++  ){
					writer.write( details[j] + ",");
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
	
	public Double[] evaluateDuplicateTag ( TestProject predictProject, TestProject trueProject ){
		ArrayList<TestReport> predictReportList = predictProject.getTestReportsInProj();
		ArrayList<TestReport> trueReportList = trueProject.getTestReportsInProj();
		
		System.out.println ( predictProject.getProjectName() + " " + trueProject.getProjectName() );
		int sameGroupSameCluster = 0, sameGroupDifCluster = 0, difGroupSameCluster = 0, difGroupDifCluster = 0;
		for ( int i =1; i < trueReportList.size(); i++ ){
			for ( int j =0; j < i; j++ ){
				String trueTagI = trueReportList.get(i).getDupTag();
				String trueTagJ = trueReportList.get(j).getDupTag();
				
				String predictTagI = predictReportList.get(i).getDupTag();
				String predictTagJ = predictReportList.get(j).getDupTag();
				
				if ( trueTagI.equals( trueTagJ) && predictTagI.equals( predictTagJ))
					sameGroupSameCluster++;
				else if ( trueTagI.equals( trueTagJ) && !predictTagI.equals( predictTagJ))
					sameGroupDifCluster++;
				else if ( !trueTagI.equals( trueTagJ) && predictTagI.equals( predictTagJ))
					difGroupSameCluster++;
				else
					difGroupDifCluster++;
			}
		}
		
		System.out.println ( sameGroupSameCluster + " " + sameGroupDifCluster + " " + difGroupSameCluster + " " + difGroupDifCluster ); 
		Double randIndex =( 1.0* (sameGroupSameCluster + difGroupDifCluster)) / ( sameGroupSameCluster + sameGroupDifCluster+ difGroupSameCluster + difGroupDifCluster );
		Double JC = sameGroupSameCluster*1.0 / ( sameGroupSameCluster+ sameGroupDifCluster+ difGroupSameCluster );
		
		Double[] performance = {randIndex, JC};
		return performance;
	}
	
	public void evaluationDuplicateTagForProjects ( String predictFolderName, String trueFolderName ){
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> predictProjectList = reader.loadTestProjectList( predictFolderName );
		ArrayList<TestProject> trueProjectList = reader.loadTestProjectList( trueFolderName );
		System.out.println( predictProjectList.size() + " " + trueProjectList.size() );
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( "data/output/performanceLightPred/duplicateEvaluation.csv" )));
			
			for ( int i =0; i < trueProjectList.size(); i++ ){
				String trueProjectName = trueProjectList.get( i).getProjectName();
				TestProject trueProject = trueProjectList.get( i );
				TestProject predictProject = null;
				
				Boolean isFind = false;
				for ( int j =0; j < predictProjectList.size(); j++ ){
					String predictProjectName = predictProjectList.get(j).getProjectName();
					if ( predictProjectName.contains( trueProjectName )){
						predictProject = predictProjectList.get( j );
						
						isFind = true;
						break;
					}
				}
				
				if ( isFind == false ){
					System.out.println( "*********************** could not find!");
					continue;
				}	
				
				Double[] performance = this.evaluateDuplicateTag( predictProject, trueProject );
				
				writer.write( trueProjectName + ",");
				for ( int k =0; k < performance.length; k++ ){
					writer.write( performance[k] + ",");
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
	
	public static void main ( String[] args ){
		DuplicateTagEvaluation evaluation = new DuplicateTagEvaluation();
		
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectList( Constants.projectFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
		
		for ( int i = 0; i < projectList.size(); i++ ){  //Constants.EVALUATION_TIME_SERIES_BEGIN
			TestProject project = projectList.get( i );
			evaluation.generatePredictedDuplicateTag (project);
		}
		
		//evaluation.evaluationDuplicateTagForProjects( "data/output/projectsWithPredictedDupTag", Constants.projectFolder);
		
		//TestProject project = reader.loadTestProject( "data/input/test/1-284-百度手机卫士功能体验_1463737109.csv" );
		//TestProject project = reader.loadTestProject( "data/input/projects/231-217-易约爱测试_1463738003.csv" );
	}
}
