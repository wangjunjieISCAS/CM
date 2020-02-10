package com.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.RBPredictionDynamic.RBBasePrediction;
import com.SemanticAnalysis.SimilarityToolByReport;
import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;

public class DuplicateTagEvaluationPrecisionRecall {
	public HashMap<Integer, ArrayList<Integer>> generatePredictedDuplicateStatus ( TestProject project ){
		RBBasePrediction predictionTool = new RBBasePrediction();
		
		ArrayList<HashMap<String, Integer>> reportTermsList = new ArrayList<HashMap<String, Integer>>();
		for ( int i=0 ; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			HashMap<String, Integer> curReportTerms = WordSegment.obtainUniqueTermForReport( report );
			
			reportTermsList.add( curReportTerms );
		}	
		
		HashMap<Integer, ArrayList<Integer>> predictLabel = new HashMap<Integer, ArrayList<Integer>>();
		for ( int i =0; i < reportTermsList.size(); i++ ) {
			HashMap<Integer, Double> simValueList = new HashMap<Integer, Double>();
			
			HashMap<String, Integer> reportTerms = reportTermsList.get( i );
			for ( int j =0; j < reportTermsList.size(); j++ ) {
				if ( j ==i )
					continue;
				
				HashMap<String, Integer> oldReportTerms = reportTermsList.get(j);
				Double simValue = predictionTool.obtainSimValue( reportTerms, oldReportTerms );
				simValueList.put( j, simValue );
			}
			
			List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(simValueList.entrySet());
	        Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() {
	            public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
	                return o2.getValue().compareTo(o1.getValue());
	            }
	        });
	        
	        ArrayList<Integer> predicted = new ArrayList<Integer>();
	        for ( int j=0; j < 20; j++) {
	        	predicted.add( list.get(j).getKey() );
	        }
	        predictLabel.put( i, predicted );
		}
		return predictLabel;
	}
	
	public HashMap<Integer, ArrayList<Integer>> obtainTrueDuplicateStatus ( TestProject project ) {
		HashMap<Integer, ArrayList<Integer>> trueLabel = new HashMap<Integer, ArrayList<Integer>>();
		
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		for ( int i =0; i < reportList.size(); i++ ) {
			String dupTag = reportList.get(i).getDupTag();
			
			ArrayList<Integer> trued = new ArrayList<Integer>();
			for ( int j =0; j < reportList.size(); j++ ) {
				if ( i == j )
					continue;
				if ( reportList.get(j).getDupTag().equals( dupTag )) {
					
					trued.add( j );
				}
			}
			trueLabel.put(i, trued );
		}
		return trueLabel;
	}
	
	public void computePrecisionRecall ( TestProject project ) {
		HashMap<Integer, ArrayList<Integer>> trueLabel = this.obtainTrueDuplicateStatus(project);
		HashMap<Integer, ArrayList<Integer>> predictLabel = this.generatePredictedDuplicateStatus(project);
		
		//对于trueLabel中，没有duplicate的reports，不进行衡量了
		int validNum = 0;
		int recallOne = 0,  recallThree =0, recallFive = 0, recallTen = 0;
		Double precisionOne = 0.0, precisionThree = 0.0, precisionFive = 0.0, precisionTen = 0.0;
		/*
		ArrayList<Double> precisionOne  = new ArrayList<Double>();
		ArrayList<Double> precisionThree  = new ArrayList<Double>();
		ArrayList<Double> precisionFive  = new ArrayList<Double>();
		ArrayList<Double> precisionTen = new ArrayList<Double>();
		*/
		for ( Integer index: predictLabel.keySet() ){
			ArrayList<Integer> predicts = predictLabel.get( index );
			ArrayList<Integer> trues = trueLabel.get( index );
			
			if ( trues.size() == 0 )
				continue;
			
			for ( int i =0; i < predicts.size() && i < 1; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recallOne ++;
					break;
				}
			}
			int precision = 0;
			for ( int i =0; i < predicts.size() && i < 1; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					precision++;
				}
			}
			precisionOne += precision / 1.0 ;
					
			for ( int i =0; i < predicts.size() && i < 3; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recallThree ++;
					break;
				}
			}
			precision = 0;
			for ( int i =0; i < predicts.size() && i < 3; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					precision++;
				}
			}
			if ( trues.size() > 3 )
				precisionThree += precision/3.0;
			else
				precisionThree += 1.0*precision/trues.size();
			
			for ( int i =0; i < predicts.size() && i < 5; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recallFive ++;
					break;
				}
			}
			precision = 0;
			for ( int i =0; i < predicts.size() && i < 5; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					precision++;
				}
			}
			if ( trues.size() > 5 )
				precisionFive += precision/5.0;
			else
				precisionFive += 1.0*precision/trues.size();
			
			for ( int i =0; i < predicts.size() && i < 10; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recallTen++;
					break;
				}
			}
			precision = 0;
			for ( int i =0; i < predicts.size() && i < 10; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					precision++;
				}
			}
			if ( trues.size() > 10 )
				precisionTen += precision/10.0 ;
			else
				precisionTen += 1.0*precision/ trues.size();
			
			validNum ++;
		}	
		
		Double recallOneRatio = 1.0*recallOne / validNum;
		Double recallThreeRatio = 1.0*recallThree / validNum;
		Double recallFiveRatio = 1.0*recallFive / validNum;
		Double recallTenRatio = 1.0*recallTen / validNum;
		
		System.out.println ( recallOneRatio + " "+ recallThreeRatio + " " + recallFiveRatio + " " + recallTenRatio );
		
		Double precisionOneRatio = precisionOne / validNum;
		Double precisionThreeRatio = precisionThree / validNum;
		Double precisionFiveRatio =  precisionFive / validNum;
		Double precisionTenRatio = precisionTen / validNum;
		System.out.println ( precisionOneRatio + " "+ precisionThreeRatio + " " + precisionFiveRatio + " " + precisionTenRatio );
	
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File ( "data/output/duplicate/precisionRecall.csv" ), true ));
			
			writer.write( "recall" + "," + "1" + "," + recallOneRatio);
			writer.newLine();
			writer.write( "recall" + "," + "3" + "," +  recallThreeRatio);
			writer.newLine();
			writer.write( "recall" + "," + "5" + "," + recallFiveRatio);
			writer.newLine();
			writer.write( "recall" + "," + "10" + "," + recallTenRatio);
			writer.newLine();
			
			writer.write( "precision" + "," + "1" + "," + precisionOneRatio);
			writer.newLine();
			writer.write( "precision" + "," + "3" + "," + precisionThreeRatio);
			writer.newLine();
			writer.write( "precision" + "," + "5" + "," + precisionFiveRatio);
			writer.newLine();
			writer.write( "precision" + "," + "10" + "," + precisionTenRatio);
			writer.newLine();
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void main ( String[] args ) {
		DuplicateTagEvaluationPrecisionRecall evaluation = new DuplicateTagEvaluationPrecisionRecall();
		
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectList( Constants.projectFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
		
		for ( int i =0; i < projectList.size(); i ++ )
			evaluation.computePrecisionRecall( projectList.get(i));
	}
}
