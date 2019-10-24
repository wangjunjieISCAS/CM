package com.baselineiSENSE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;
import com.evaluation.PerformanceEvaluation;


/*
 * 除了Mth之后的，其他的CRC的变形，都用这个来实现，
 * 只需要实现相应的 M。CRCAlgorithm，并且初始化相应的M.CRCAlgorithm就行
 */
public class ClosePredictionCRCGeneral extends ClosePrediction {
	CRCAlgorithm crcAlgorithm;
	
	public ClosePredictionCRCGeneral ( String type ) {
		if ( type.equals( "MhJK")) {
			crcAlgorithm = new MhJKCRCAlgorithm();
		}
		if ( type.equals( "MtCH")) {
			crcAlgorithm = new MtCHCRCAlgorithm();
		}
		if ( type.equals( "MhCH")) {
			crcAlgorithm = new MhCHCRCAlgorithm();
		}
	}
	
	@Override
	public Double[] predictCloseTime(TestProject project, Integer[] thresList) {
		// TODO Auto-generated method stub
		int captureSize = thresList[0];
		int equalTimeThres = thresList[1];
		
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		ArrayList<Integer[]> captureResults = new ArrayList<Integer[]>();	
		TreeMap<Integer, ArrayList<TestReport>> captureProcess = new TreeMap<Integer, ArrayList<TestReport>>();
		
		PerformanceEvaluation evaluation = new PerformanceEvaluation( "data/input/bugCurveStatistics.csv");
		
		//the first equalTimeThres capture
		int captureTime = -1;
		while ( true ) {
			captureTime++;
			ArrayList<TestReport> curCapture = new ArrayList<TestReport>();
			for ( int i =0; i < captureSize; i++ ) {
				TestReport report = reportList.get( i );
				if ( report.getBugTag().equals( "审核通过")) {
					curCapture.add( report );
				}				
			}
			
			captureProcess.put( captureTime, curCapture );
			if ( captureTime > equalTimeThres ) {
				break;
			}
		}
		
		Integer[] moreResults = null, evaluateResults = null;
		while ( true ) {
			captureTime ++;
			int beginReport = captureTime * captureSize;
			int endReport = beginReport + captureSize;
			
			if ( endReport >= reportList.size() )
				break;
			
			ArrayList<TestReport> curCapture = new ArrayList<TestReport>();
			for ( int j = beginReport; j < endReport && j < reportList.size(); j++ ) {
				if ( reportList.get(j).getBugTag().equals( "审核通过")) {
					curCapture.add( reportList.get( j ));
				}				
			}
			captureProcess.put( captureTime, curCapture );
			
			Integer[] results = crcAlgorithm.obtainRecaptureResults( captureProcess);		 //NJ1, NJ2, NJ3, true totalNumBugs
			evaluateResults = new Integer[2];
			evaluateResults[0] = results[3];    //这里比较别扭，这个真实的发现了多少个不重复的缺陷，应该在主程序这里获得的，和obtainRecaptureResults没有关系
			evaluateResults[1] = endReport;
	
			moreResults = new Integer[results.length+2];
			moreResults[0] = results[0];
			moreResults[1] = results[1];
			moreResults[2] = results[2];			
			moreResults[3] = evaluateResults[0];
			moreResults[4] = evaluateResults[1];
			captureResults.add( moreResults );
			System.out.println(  "moreResults: " + results[0] + " " + results[1] + " " + results[2] + " " + moreResults[3] + " " + moreResults[4] );
			
			//captureResults.add( moreResults );
			
			Boolean isTerminate = false;
			isTerminate = this.whetherCanTerminate(captureResults, equalTimeThres );
			
			if ( isTerminate ) {
				Double[] performance = evaluation.evaluatePerformance (evaluateResults, project.getProjectName() );
				System.out.print ( "************************************ ");
				for ( int k =0; k < performance.length; k++ )
					System.out.print( performance[k] + " " );
				System.out.println( );
				
				return performance;
			}			
		}
		
		Double[] performance = evaluation.evaluatePerformance( evaluateResults, project.getProjectName() );
		return performance;
	}

	@Override
	public void predictCloseTimeForProjects(String folderName, String performanceFile, Integer[] thresList) {
		// TODO Auto-generated method stub
		super.predictCloseTimeForProjects(folderName, performanceFile, thresList);
	}

	public Boolean whetherCanTerminate ( ArrayList<Integer[]> captureResults, Integer equalTimeThres ) {   //NJ1, NJ2, NJ3, true totalNumBugs
		if ( captureResults.size() < equalTimeThres )
			return false;
		
		int count = 0;
		for ( int i = captureResults.size()-1; i > 0 ; i-- ) {
			Integer[] curResults = captureResults.get( i);
			Integer[] priorResults = captureResults.get( i-1 );
			
			if ( this.isEqual(priorResults, curResults) == true )
				count ++;
			else
				count = 0;
			
			if ( count >= equalTimeThres )
				return true;
		}
		return false;
	}
	
	
	public Boolean isEqual ( Integer[] priorResults, Integer[] curResults) {   //use NJ1
		//little trick for MhJK
		/*
		int truePrior = 0, minDif = 1000;
		for ( int i =0; i <2; i++ ) {
			if ( priorResults[3] - priorResults[i] < minDif) {
				minDif = priorResults[3] - priorResults[i];
				truePrior = priorResults[i];
			}
		}
		int trueCur = 0;
		minDif = 1000;
		for ( int i =0; i <2; i++ ) {
			if ( curResults[3] - curResults[i] < minDif) {
				minDif = curResults[3] - curResults[i];
				trueCur = curResults[i];
			}
		}
		
		if ( truePrior != trueCur )
			return false;
		if ( trueCur != curResults[3])
			return false;
		if ( curResults[3] == 0 )
			return false;
		return true;
		*/
		
		//if ( priorResults[0] != curResults[0] )    // FSE version has this statement
			//return false;
		//predicted total number of bugs do not equal with total reported number of bugs
		//*** For MhJK, do not need this IF
		if ( curResults[0] != curResults[3] )     
			return false;
			
		if ( curResults[3] ==0 )
			return false;
		
		return true;
	}
	
	
	public static void main ( String args[] ) {
		String type = "MhCH";   //MhJK, MtCH, MhCH
		ClosePredictionCRCGeneral prediction = new ClosePredictionCRCGeneral( type );	
		
		int beginIndex = 3, endIndex = 20;
		Integer[] equalTimeThres = {2};   //, 2, 3, 4 };
		for ( int i = beginIndex; i <= endIndex; i++ ) {
			for ( int j =0; j < equalTimeThres.length; j++) {
				Integer[] thresList = { i, equalTimeThres[j]}; 
		
				String tag = thresList[0].toString();
				prediction.predictCloseTimeForProjects( Constants.projectFolder, "data/output/performanceiSENSE/" + type + "/performance" + type + "-" + tag + ".csv",thresList );
			}
		}
	}
}
