package com.baselineiSENSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.evaluation.PerformanceEvaluation;

public class ClosePredictionCRCMth extends ClosePrediction {
	Double bugPercentThresForTimeDist = 0.8;
	/*
	 * 基于Capture Recapture中的Mth，就是既考虑各个缺陷有不同的被检测出来的概率；又考虑不同的人有不同的缺陷发现概率
	 */	
	@Override
	public Double[] predictCloseTime(TestProject project, Integer[] thresList) {
		// TODO Auto-generated method stub
		int captureSize = thresList[0];
		int equalTimeThres = thresList[1];
	
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		ArrayList<Integer[]> captureResults = new ArrayList<Integer[]>();	
		TreeMap<Integer, ArrayList<TestReport>> captureProcess = new TreeMap<Integer, ArrayList<TestReport>>();
		
		PerformanceEvaluation evaluation = new PerformanceEvaluation( "data/input/bugCurveStatistics.csv");
		
		MthCRCAlgorithm MthAlgorithm = new MthCRCAlgorithm();
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
			
			Integer[] results = MthAlgorithm.obtainRecaptureResults( captureProcess);		 //N1, N2, N3, true totalNumBugs
			evaluateResults = new Integer[2];
			evaluateResults[0] = results[3];
			evaluateResults[1] = endReport;
	
			moreResults = new Integer[results.length+2];
			moreResults[0] = results[0];
			moreResults[1] = results[1];
			moreResults[2] = results[2];			
			moreResults[3] = evaluateResults[0];
			moreResults[4] = evaluateResults[1];
			captureResults.add( moreResults );
			System.out.println(  "moreResults: " + results[0] + " " + results[1] + " " + results[2] + " " + moreResults[3] + " " + moreResults[4] );
			
			//captureResults.add( moreResults );      //wrong in FSE
			//with this statement, the performance is a little better, 
			//it can also be done through deleting the statement 'if ( priorResults[2] != curResults[2] )' in isEqual
			
			Boolean isTerminate = this.whetherCanTerminate(captureResults, equalTimeThres );
			
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

	public Boolean whetherCanTerminate ( ArrayList<Integer[]> captureResults, Integer equalTimeThres ) {
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
	
	public Boolean isEqual ( Integer[] priorResults, Integer[] curResults) {
		//if ( priorResults[2] != curResults[2] )
			//return false;
		//predicted total number of bugs do not equal with total reported number of bugs
		//if the predicted number of bugs is smaller than the actual number, then treat it as equal
		//not so good performance
		//if ( curResults[2] != curResults[3] && curResults[2] > curResults[3] )     
			//return false;
		if ( curResults[2] != curResults[3] )     
			return false;
		if ( curResults[3] ==0 )
			return false;
		return true;
	}
	
	
	public static void main ( String args[] ) {
		ClosePredictionCRCMth prediction = new ClosePredictionCRCMth();
		//Integer[] captureSize = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		
		int beginIndex = 3, endIndex = 20;
		Integer[] equalTimes = {2};
		for ( int i = beginIndex; i <= endIndex; i++ ) {
			for ( int j =0; j < equalTimes.length; j++ ){
				Integer[] thresList = { i, equalTimes[j]};
				String tag = thresList[0].toString() + "-" + thresList[1].toString();
				
				prediction.predictCloseTimeForProjects(Constants.projectFolder, "data/output/performanceiSENSE/Mth/performanceMth-" + tag + ".csv", thresList );
			}				
		}
		
		/*
		int beginIndex = 3, endIndex = 32;
		Integer[] candPara = new Integer[endIndex-beginIndex+1];
		for ( int i = beginIndex; i <= endIndex; i++ ) {
			candPara[i-beginIndex] = i;
		}
		prediction.crossValidationForProjects( "data/input/projects", candPara, "data/output/crossPerformanceMth", 100 );
		*/
	}
}
