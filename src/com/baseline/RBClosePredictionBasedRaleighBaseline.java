package com.baseline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.RBPredictionDynamic.RBBasePrediction;
import com.RBPredictionDynamic.WriterTool;
import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.evaluation.PerformanceEvaluation;

public class RBClosePredictionBasedRaleighBaseline extends RBBasePrediction{

	public RBClosePredictionBasedRaleighBaseline() {
		// TODO Auto-generated constructor stub
	}
	
	public Double[] conductClosePrediction ( TestProject project, String[] thresList ) {
		Integer constReportNumlThres = Integer.parseInt(thresList[0]);
		Double simThres = Double.parseDouble( thresList[1] );
		
		PerformanceEvaluation evaluation = new PerformanceEvaluation( );

		ArrayList<Integer> bugTagList = new ArrayList<Integer>();
		//对于第i个报告，计算其和前面所有报告的相似性，选择相似性最大的（假设为j），如果该相似性值大于阈值，则第i个报告的dupTag为报告j的dupTag；否则，启用新的dupTag
		ArrayList<Integer> reportPredDupTag = new ArrayList<Integer>();
		HashSet<String> noDupTagList = new HashSet<String>();
		
		ArrayList<HashMap<String, Integer>> histReportTermsList = new ArrayList<HashMap<String, Integer>>();
		
		Integer curGroupNum = 0;
		HashMap<Integer, String> noDupReportList = new HashMap<Integer, String>();
		HashSet<String> noDupReport = new HashSet<String>();
		for ( int i=0 ; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			HashMap<String, Integer> curReportTerms = WordSegment.obtainUniqueTermForReport( report );
			
			if ( curReportTerms.size() <= 1 ){     //该规则作为是否为bug的判断
				noDupReportList.put( i, "no");
				reportPredDupTag.add( -2 );
				
				continue;
			}
			
			Integer dupTag = this.obtainDupTagInfo(curReportTerms, histReportTermsList, reportPredDupTag, simThres);
			if ( dupTag == -1 ) {
				dupTag = ++ curGroupNum;
				noDupReportList.put( i, "bug");
				noDupReport.add( dupTag.toString() );
			}
			else{
				noDupReportList.put( i, "no");
			}
			
			reportPredDupTag.add( dupTag );
			histReportTermsList.add( curReportTerms );
		}	
		
		//System.out.println( "===================== " + noDupReport.size() );
		TreeMap<Integer, Integer> timeDistList = new TreeMap<Integer, Integer>();
		//based on the contant number of bugs
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			String finalTag = noDupReportList.get( i );
			
			int interval = i / constReportNumlThres;
			int count = 0;
			if ( finalTag.equals( "bug")) {
				count = 1;			
			}
			if ( timeDistList.containsKey( interval )) {
				count += timeDistList.get( interval );
			}	
			timeDistList.put( interval, count );
		}
		System.out.println( timeDistList.toString() );
		
		//find the i point when the i+1 point is smaller than i
		int priorBugNum = 0, maxIndex = -1, maxBugNum = -1;
		for ( Integer interval : timeDistList.keySet() ) {
			Integer number = timeDistList.get( interval );
			if ( priorBugNum <= number) {
				priorBugNum = number;
				continue;
			}else {
				maxIndex = interval;
				maxBugNum = priorBugNum;
				break;
			}
		}
		
		System.out.print( maxIndex + " " + maxBugNum + " " );
		Integer C = maxIndex * maxIndex * 2;
		
		DecimalFormat df = new DecimalFormat("######0"); //四色五入转换成整数
		ArrayList<Integer> totalBugList = new ArrayList<Integer>();
		
		int sumTotalBugs =0, numSum = 0;
		//将前面是0的去掉
		for ( int t = 1; t <= maxIndex; t++) {
			if ( !timeDistList.containsKey( t-1 ))
				continue;
			
			Integer ft = timeDistList.get( t-1 );
			
			double bugs = C * Math.exp( (1.0 * t*t) / C) * ft / (2*t) ;    //predicted total number of bugs
			Integer totalBugs = Integer.parseInt(df.format( bugs ));
			totalBugList.add( totalBugs );
			if ( totalBugs == 0 ) {
				continue;
			}
			
			sumTotalBugs += totalBugs;
			numSum ++;
			
			System.out.print( totalBugs + " " );
		}
		System.out.println( );
		Integer dynPredictBug = 0;
		if ( numSum != 0 )
			dynPredictBug = (int)sumTotalBugs / numSum ;
		System.out.println( "dynPredictBug : " + dynPredictBug );
		
		Integer endReportNum = this.obtainEndReport( noDupReportList, dynPredictBug );
		
		//i为接收到的report数目
		Double[] evaPerformance = evaluation.evaluatePerformanceByEndReport ( endReportNum, project );
		Double[] performance = new Double[evaPerformance.length+1];
		performance[evaPerformance.length] = noDupTagList.size()*1.0;
		System.arraycopy( evaPerformance, 0, performance, 0, evaPerformance.length );
		System.out.println ( evaPerformance.length + " " + performance.length );
		for ( int k =0; k < performance.length; k++ )
			System.out.print( performance[k] + " " );
		System.out.println( );
		
		//WriterTool writerTool = new WriterTool();
		//String tagOutputFile = "data/output/projectsWithPredictedDupTag/" + project.getProjectName();
		//writerTool.outputPredictionDupTag(project, bugTagList, reportPredDupTag, tagOutputFile );
		
		return performance;	
	}
	
	public Integer obtainEndReport ( HashMap<Integer, String> noDupReportList, Integer finalBugNum ) {
		int endReport = noDupReportList.size();
		int count = 0;
		for ( int i =0; i < noDupReportList.size(); i++ ){
			String tag = "no";
			if ( noDupReportList.containsKey( i )){
				tag = noDupReportList.get( i );
			}
			if ( tag.equals( "bug")){
				count++;
			}
			if ( count >= finalBugNum ){
				endReport = i;
				break;
			}
		}
		
		return endReport;
	}
	
	public static void main ( String[] args ) {
		RBClosePredictionBasedRaleighBaseline closePrediction = new RBClosePredictionBasedRaleighBaseline();
		
		Integer[] constantThresList = { 14 };
		Double[] simThresList = { 0.7 };
		for ( int i =0; i < constantThresList.length; i++ ){
			for ( int j =0; j < simThresList.length; j++ ){
				String performanceFile = "data/output/performanceRaleigh/performance-Raleigh" + constantThresList[i] + "-" + simThresList[j] + ".csv";
				String[] thresList = { constantThresList[i].toString() , simThresList[j].toString() };
				closePrediction.predictCloseTimeForProjects( Constants.projectFolder, Constants.taskFolder, performanceFile, thresList);
			}
		}
	}
}
