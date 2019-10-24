package com.baselineiSENSE;

import java.util.ArrayList;
import java.util.HashSet;

import com.data.TestProject;
import com.data.TestReport;
import com.evaluation.PerformanceEvaluation;

public class ClosePredictionKneeMethod extends ClosePrediction{

	@Override
	public Double[] predictCloseTime(TestProject project, Integer[] thresList) {
		// TODO Auto-generated method stub
		//System.out.println( thresList.length );
		Double gradThres = (1.0*thresList[0])/10.0;
		Integer stepSize = thresList[1];
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		PerformanceEvaluation evaluation = new PerformanceEvaluation( "data/input/bugCurveStatistics.csv");
		
		HashSet<String> noDupTagList = new HashSet<String>();
		
		ArrayList<Integer> curBugList = new ArrayList<Integer>();
		ArrayList<Integer> curReportList = new ArrayList<Integer>();
		curBugList.add( 0);
		curReportList.add( 0 );
		
		int endReport = 0;
		for ( int i =0; i < reportList.size(); i+= stepSize ) {
			if ( i >= reportList.size() )
				break;
			
			endReport = i+ stepSize;
			curReportList.add( endReport );
			
			for ( int j =i; j < endReport; j++ ) {
				TestReport report = reportList.get( j );
				String tag = report.getBugTag();
				String dupTag = report.getDupTag();
				
				if ( tag.equals( "ÉóºËÍ¨¹ý") && !noDupTagList.contains( dupTag )) {
					noDupTagList.add( dupTag );
				}
			}
			curBugList.add( noDupTagList.size() );
			
			Integer[] curBugNumber = (Integer[]) curBugList.toArray( new Integer[curBugList.size()]);
			Integer[] curReportNumber = (Integer[]) curReportList.toArray(new Integer[curReportList.size()]);
			boolean isTerminate = this.whetherCanTerminate(curBugNumber, curReportNumber, gradThres);
			if ( isTerminate == true )
				break;
		}
		
		int bugNum = noDupTagList.size();
		Integer[] results = { bugNum, endReport };
		Double[] performance = evaluation.evaluatePerformance ( results, project.getProjectName() );
		return performance;
	}
	
	public Boolean whetherCanTerminate ( Integer[] curBugNumber, Integer[] curReportNumber,  Double gradThres ) { 
		Integer bugPos = curBugNumber[curBugNumber.length-1];
		Integer reportPos = curReportNumber[curReportNumber.length-1];
		
		Double ratio = 1.0* reportPos / ( Math.sqrt( bugPos*bugPos + reportPos*reportPos));
		Double per_best = -1.0;
		Integer best = 0;
		for ( int i = curReportNumber.length-1; i >=0 ; i-- ) {
			Double per = ( curBugNumber[i] - curReportNumber[i] * bugPos / reportPos ) * ratio;
			if ( per > per_best ) {
				best = i;
				per_best = per;
			}
		}
		
		Integer kneePoint = best;
		Double rho = ( reportPos - curReportNumber[best]) * curBugNumber[best] / curReportNumber[best] / ( 1.0 + bugPos - curBugNumber[best]);
		if ( rho >= gradThres ) {
			return true;
		}
		return false;
	}
	

	@Override
	public void predictCloseTimeForProjects(String folderName, String performanceFile, Integer[] thresList) {
		// TODO Auto-generated method stub
		super.predictCloseTimeForProjects(folderName, performanceFile, thresList);
	}
	
	public static void main ( String args[] ) {
		ClosePredictionKneeMethod prediction = new ClosePredictionKneeMethod();
		
		int beginIndex = 30;
		int size = 30, step = 2;
		int index = 0;
		for ( int i = beginIndex; index < size; i+= step ) {
			Integer[] thresList = { i, 1};
			String tag = thresList[0].toString() + "-" + thresList[1].toString();
			
			prediction.predictCloseTimeForProjects("data/input/projects", "data/output/performanceKnee/performanceKnee-" + tag + ".csv",thresList);
			index++;
		}
				
		/*
		int beginIndex = 30;
		int size = 30, step = 2;
		Integer[] candPara = new Integer[size];
		int index = 0;
		for ( int i = beginIndex; index < size; i+= step ) {
			candPara[index++] = i;
		}
		prediction.crossValidationForProjects( "data/input/projects", candPara, "data/output/crossPerformanceKnee", 100 );
		*/
		/*
		 * 
		Integer[] curBugNumber = {0,3,5,7,11, 12,14, 14, 14, 14, 14};
		Integer[] curReportNumber = {0,10,20,30,40,50,60,70,80,90, 100};
			
		ClosePredictionKneeMethod prediction = new ClosePredictionKneeMethod();
		for ( int i =3; i < curBugNumber.length; i++ ) {
			Integer[] tempBugNumber = new Integer[i];
			Integer[] tempReportNumber = new Integer[i];
			for ( int j =0; j < i ; j++ ) {
				tempBugNumber[j] = curBugNumber[j];
				tempReportNumber[j] = curReportNumber[j];
			}
			Boolean flag = prediction.whetherCanTerminate(tempBugNumber, tempReportNumber, 6);
			System.out.println ( i + " " + flag);
		}
		 */
		
	}
}
