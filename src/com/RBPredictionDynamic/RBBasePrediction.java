package com.RBPredictionDynamic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.SemanticAnalysis.SimilarityToolByReport;
import com.SemanticAnalysis.WordEmbeddingTool;
import com.data.Constants;
import com.data.TestProject;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;

public class RBBasePrediction {
	HashMap<String, ArrayList<Double>> termEmbList;
	
	public RBBasePrediction() {
		// TODO Auto-generated constructor stub
		WordEmbeddingTool embTool = new WordEmbeddingTool();
		termEmbList = embTool.retrieveWordEmbedding();
	}
	
	public Double[] conductClosePrediction ( TestProject project, String[] thresList ){
		return null;
	}
	
	public void predictCloseTimeForProjects (  String projectFolder, String taskFolder, String performanceFile, String[] thresList  ) {
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectAndTaskList(Constants.projectFolder, Constants.taskFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( performanceFile ), true));
			
			writer.write( "project" + "," + "bugsDetected" + "," + "totalBugs" + "," + "percentBugsDetected" + "," + "reportsSubmit" + "," + "totalReports" + "," 
					+ "percentReportsSubmit" + "," + "percentSavedEffort" + "," + "F1" + "," + "optimalReportsSubmit" + "," + "differenceWithOptimal" );
			writer.newLine();
			
			for ( int i = Constants.EVALUATION_TIME_SERIES_BEGIN; i< projectList.size(); i++ ){    //********  Constants.EVALUATION_TIME_SERIES_BEGIN
				TestProject project = projectList.get( i );				
				Double[] performance = this.conductClosePrediction(project, thresList );
				
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
	
	public Integer obtainDupTagInfo ( HashMap<String, Integer> curReportTerms, ArrayList<HashMap<String, Integer>> histReportTermsList, ArrayList<Integer> reportPredDupTag, Double simThres ){
		if ( histReportTermsList.size() <= 0 )
			return 0;
		
		SimilarityToolByReport simTool = new SimilarityToolByReport(  );
		Integer maxTag = simTool.similarityBasedWordEmbedding(curReportTerms, histReportTermsList, simThres, termEmbList );
		
		int groupTag = maxTag;
		if ( maxTag != -1 ) {
			groupTag = reportPredDupTag.get( maxTag );
		}		
		
		return groupTag;
	}
	
	//衡量duplicateTag性能时用到
	public Double obtainSimValue ( HashMap<String, Integer> queryReportTerms, HashMap<String, Integer> oldReportTerms ) {
		SimilarityToolByReport simTool = new SimilarityToolByReport(  );
		ArrayList<Double> queryEmbList = simTool.sentenceToVecNotConsiderFreq(termEmbList, queryReportTerms);
		ArrayList<Double> oldEmbList = simTool.sentenceToVecNotConsiderFreq(termEmbList, oldReportTerms);
		
		Double simValue = simTool.cosineSimilarityForList( queryEmbList, oldEmbList );
		
		return simValue;
	}
	
}
