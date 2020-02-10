package com.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.RBPredictionCoverage.CoverageMeasurement;
import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;

public class TermCoverageEvaluation {
	CoverageMeasurement coverageCheck = new CoverageMeasurement ();
	
	public Double obtainTermCoveragePoint ( Double termCovThres, TestProject project ) {
		ArrayList<String> taskTerms = project.getTestTask().getTaskDescription();
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		ArrayList<HashMap<String, Integer>> histReportTermsList = new ArrayList<HashMap<String, Integer>>();
		int covPoint = reportList.size()-1;
		for ( int i =0; i < reportList.size(); i++ ) {
			HashMap<String, Integer> reportTerms = WordSegment.obtainUniqueTermForReport( reportList.get(i) );
			histReportTermsList.add( reportTerms );
			
			Double coverageRatio = coverageCheck.measureCurrentCoverage(taskTerms, histReportTermsList );
			//System.out.println (coverageRatio + " " + termCovThres );
			if ( coverageRatio >= termCovThres ) {
				covPoint = i ;
				break;
			}
		}
		
		//根据这个点，找到发现缺陷的比例
		HashSet<String> uniqueBugSet = new HashSet<String>();
		HashSet<String> detectBugSet = new HashSet<String>();
		for ( int i =0; i < reportList.size(); i++ ) {
			String bug = reportList.get(i).getBugTag();
			String dup = reportList.get(i).getDupTag();
			if ( bug.equals("审核通过")) {
				uniqueBugSet.add( dup );
				if ( i <= covPoint ) {
					detectBugSet.add( dup );
				}
			}
		}
		Double bugRatio = (1.0*detectBugSet.size() )/ uniqueBugSet.size();
		return bugRatio;
	}
	
	public void obtainTermCoveragePerformance ( ArrayList<TestProject> projectList ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( "data/output/termCoverage/termCov.csv" )));
			
			//writer.write( " " +"," + "  ");
			//writer.newLine();
			Double beginCov = 0.05;
			Double endCov = 1.0;
			Double covThres = beginCov;
			writer.write(" " + ",");
			while ( covThres < endCov ){
				writer.write( covThres + ",");
				covThres += 0.05;
			}
			writer.newLine();
			
			for ( int i =0; i < projectList.size() ; i++ ) {    //projectList.size() 
				TestProject project = projectList.get( i );
				System.out.println ( project.getProjectName() ); 
				
				writer.write( project.getProjectName() + ",");
				covThres = beginCov;
				while ( covThres < endCov ){
					Double oriBugRatio = this.obtainTermCoveragePoint( covThres, project);
					writer.write( oriBugRatio + ",");
					
					covThres += 0.05;
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
	
	public static void main ( String[] args ) {
		TermCoverageEvaluation termCovEval = new TermCoverageEvaluation();
		
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectAndTaskList(Constants.projectFolder, Constants.taskFolder);
		
		termCovEval.obtainTermCoveragePerformance(projectList);
	}
}
