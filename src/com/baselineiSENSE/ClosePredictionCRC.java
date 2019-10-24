package com.baselineiSENSE;

import java.util.ArrayList;
import java.util.HashSet;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.evaluation.PerformanceEvaluation;

public class ClosePredictionCRC extends ClosePrediction{
	Double bugPercentThresForTimeDist = 0.8;
	Double toalBugPercentTerminate = 0.95;
	
	/*
	 * 基于最简单的capture-recapture
	 * Given those conditions, the estimated population size N = MC/R, where:
		N = Estimate of total population size
		M = Total number of animals captured and marked on the first visit 
		C = Total number of animals captured on the second visit 
		R = Number of animals captured on the first visit that were then recaptured on the second visit
	 */
	
	@Override
	public Double[] predictCloseTime(TestProject project, Integer[] thresList) {
		// TODO Auto-generated method stub
		
		int captureSize = thresList[0];
		int equalTimeThres = thresList[1];
		int timeIntervalThres = 0;
		
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		ArrayList<Integer[]> captureResults = new ArrayList<Integer[]>();		
		PerformanceEvaluation evaluation = new PerformanceEvaluation( "data/input/bugCurveStatistics.csv");
		HashSet<String> noDupReportList = new HashSet<String>();
		
		//1st capture
		ArrayList<TestReport> priorReportList = new ArrayList<TestReport>();
		for ( int i =0; i < captureSize; i++ ) {
			TestReport report = reportList.get( i );
			priorReportList.add( report );
			if ( report.getBugTag().equals( "审核通过") ) {
				noDupReportList.add( report.getDupTag() );
			}			
		}
		
		int i = 0;
		Integer[] moreResults = null, evaluateResults = null;
		while ( true ) {
			i++;
			int beginReport = i* captureSize;
			int endReport = beginReport + captureSize;
			
			if ( endReport >= reportList.size() )
				break;
			
			ArrayList<TestReport> curReportList = new ArrayList<TestReport>();
			for ( int j = beginReport; j < endReport && j < reportList.size(); j++ ) {
				TestReport report = reportList.get( j);
				curReportList.add( report );
				if ( report.getBugTag().equals( "审核通过") ) {
					noDupReportList.add( report.getDupTag() );
				}
			}
			
			Integer[] results = this.obtainRecaptureResults(priorReportList, curReportList);		 //priorCapSize, curCapSize, curRecapSize
			evaluateResults = new Integer[2];	
			evaluateResults[0] = noDupReportList.size();
			//System.out.println( evaluateResults[0] );
			evaluateResults[1] = endReport;
			
			moreResults = new Integer[results.length+1];
			moreResults[0] = results[0];
			moreResults[1] = results[1];
			moreResults[2] = results[2];		
			int estPopSize = 0;
			if ( results[2] == 0 )
				estPopSize = results[0] + results[1];
			else
				estPopSize = (int) ( (results[0] * results[1]) / results[2] );
			moreResults[3] = estPopSize;
			captureResults.add( moreResults );
			
			System.out.println( "estPopSize is: " + estPopSize +"; priorCapSize is: " + results[0] + "; curCapSize is: " + results[1] + "; curRecapSize is: " + results[2] + 
					"; endReportNumber is: " + endReport + " " + noDupReportList.size());
			
			Boolean isTerminate = this.whetherCanTerminate(captureResults, equalTimeThres );
			
			if ( isTerminate ) {
				Double[] performance = evaluation.evaluatePerformance(evaluateResults, project.getProjectName() );
				System.out.print ( "************************************ ");
				for ( int k =0; k < performance.length; k++ )
					System.out.print( performance[k] + " " );
				System.out.println( );
				
				return performance;
			}
			
			priorReportList.addAll( curReportList );
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
			
			if ( this.isEqualCondition(priorResults, curResults) == true )
				count ++;
			else
				count = 0;
			
			if ( count >= equalTimeThres )
				return true;
		}
		return false;
	}
	
	public Boolean isEqual ( Integer[] priorResults, Integer[] curResults) {
		if ( priorResults[3] != curResults[3] )
			return false;
		if ( curResults[1] != curResults[2] )    //curRecapSize != curCapSize, this indicates newBugs are detected in curCapture, so even results remain same, still return false
			return false;
		int captureBug = curResults[0] + curResults[1] - curResults[2] ;
		if ( captureBug != curResults[3] )    //estPopsize != all capture bug size
			return false;
		
		if ( captureBug == 0 )   //bug number is 0
			return false;
		
		return true;
	}
	
	//priorCapSize, curCapSize, curRecapSize, estPopSize
	public Boolean isEqualCondition ( Integer[] priorResults, Integer[] curResults ) {
		int captureBug = curResults[0] + curResults[1] - curResults[2];
		if ( captureBug == 0 )
			return false;
		
		if ( priorResults[3] <= curResults[3]-1  )
			return false;
		
		if ( captureBug <= curResults[3] * this.toalBugPercentTerminate )
			return false;
		
		return true;
	}
	
	
	public Integer[] obtainRecaptureResults ( ArrayList<TestReport> priorReportList, ArrayList<TestReport> curReportList ) {
		HashSet<String> priorNoDupBugs = new HashSet<String>();
		HashSet<String> curNoDupBugs = new HashSet<String>();
		
		for ( int i=0; i < priorReportList.size(); i++ ) {
			TestReport report = priorReportList.get( i );
			if ( report.getBugTag().equals( "审核通过")) {
				priorNoDupBugs.add( report.getDupTag() );
			}
		}
		
		for ( int i =0; i < curReportList.size(); i++ ) {
			TestReport report = curReportList.get( i );
			if ( report.getBugTag().equals( "审核通过")) {
				curNoDupBugs.add( report.getDupTag() );
			}
		}
		
		int priorCapSize = priorNoDupBugs.size();
		int curCapSize = curNoDupBugs.size();
		int curRecapSize = 0;
		for ( String dupId : curNoDupBugs ) {
			if ( priorNoDupBugs.contains( dupId )) {
				curRecapSize++;
			}
		}
		
		Integer[] results = new Integer[3];
		results[0] = priorCapSize;
		results[1] = curCapSize;
		results[2] = curRecapSize;
		
		return results;
	}
	
	
	
	public static void main ( String args[] ) {
		ClosePredictionCRC prediction = new ClosePredictionCRC();
		
		//Integer[] captureSize = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };	
		int beginIndex = 3, endIndex = 20;   //3 - 32
		Integer[] equalTimeThres = {2};   //, 2, 3, 4 };
		for ( int i = beginIndex; i <= endIndex; i++ ) {
			for ( int j =0; j < equalTimeThres.length; j++) {
				Integer[] thresList = { i, equalTimeThres[j]};
				String tag = thresList[0].toString() + "-" + thresList[1].toString();
				prediction.predictCloseTimeForProjects(  Constants.projectFolder, "data/output/performanceiSENSE/M0/performance" + "M0" + "-" + tag + ".csv",thresList  );
			}
		}
		
		/*
		int beginIndex = 3, endIndex = 32;
		Integer[] candPara = new Integer[endIndex-beginIndex+1];
		for ( int i = beginIndex; i <= endIndex; i++ ) {
			candPara[i-beginIndex] = i;
		}
		prediction.crossValidationForProjects( "data/input/projects", candPara, "data/output/crossPerformanceM0-95", 100 );
		*/
	}
}
 