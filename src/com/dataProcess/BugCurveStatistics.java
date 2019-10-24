package com.dataProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;

public class BugCurveStatistics {
	public Integer[] obtainBugNumInspectNumReportNum ( TestProject project ){
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		HashSet<String> uniqueBugTagList = new HashSet<String>();
		int insReportNum = 0;
		for ( int i=0; i < reportList.size(); i++ ){
			TestReport report = reportList.get( i );
			String bugTag = report.getBugTag();
			String dupTag = report.getDupTag();
			if ( bugTag.equals("ÉóºËÍ¨¹ý")){
				if ( !uniqueBugTagList.contains( dupTag )){
					insReportNum = i;
					uniqueBugTagList.add( dupTag );
				}
			}
		}
		
		return new Integer[] { uniqueBugTagList.size(), insReportNum, reportList.size() };
	}
	
	public void outputBugCurveStatistics ( ){
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectAndTaskList(Constants.projectFolder, Constants.taskFolder );
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( "data/input/bugCurveStatistics.csv" ), true));
			
			writer.write( "project" + "," + "bugNum" +"," + "inspectionReportNum" + "," + "totalReportNum" );
			writer.newLine();
			
			for ( int i =0; i < projectList.size(); i++ ){
				TestProject project = projectList.get( i );
				Integer[] result = this.obtainBugNumInspectNumReportNum(project);
				
				writer.write( project.getProjectName() + ",");
				for ( int j =0; j < result.length; j++ ){
					writer.write( result[j] + ",");
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
		BugCurveStatistics statisTool = new BugCurveStatistics ();
		statisTool.outputBugCurveStatistics();
	}
}
