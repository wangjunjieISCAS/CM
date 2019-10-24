package com.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.data.TestProject;
import com.data.TestReport;


public class PerformanceEvaluation {
	HashMap<String, Integer[]> groundTruthMap ;
	
	public PerformanceEvaluation ( ){
		
	}
	public PerformanceEvaluation ( String fileName ) {
		groundTruthMap = new HashMap<String, Integer[]>();
		try {
			BufferedReader reader = new BufferedReader ( new FileReader ( new File ( fileName ) ));
			
			String line = "";
			boolean isHeader = true;
			while ( (line = reader.readLine()) != null ) {
				if ( isHeader == true ) {
					isHeader = false;
					continue;
				}
				String[] temp = line.split( ",");
				String project = temp[0].trim();
				
				Integer bugNum = Integer.parseInt( temp[1].trim() );
				Integer insNum = Integer.parseInt( temp[2].trim() );
				Integer reportNum = Integer.parseInt( temp[3].trim() );
				
				Integer[] groundTruth = new Integer[3];
				groundTruth[0] = bugNum;
				groundTruth[1] = insNum;
				groundTruth[2] = reportNum;

				groundTruthMap.put( project, groundTruth );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Double[] evaluatePerformance ( Integer[] results, String project ) { 
		System.out.println (  project + "**********************");
		Integer[] groundTruth = groundTruthMap.get( project );     //bugNum, insNum, reportNum
		if ( results == null || results.length == 0 ) {
			Double[] performance = { 0.0, 1.0*groundTruth[0], 0.0, 0.0, 1.0*groundTruth[2], 0.0, 1.0, 
					0.0, 0.0,  1.0*groundTruth[1], 0.0  };
			return performance;
		}			
		
		int bugDetected = results[0];   
		if ( bugDetected > groundTruth[0] )
			bugDetected = groundTruth[0];
		double percentBug = (1.0*bugDetected) / (1.0* groundTruth[0]);
		
		int reportSubmit = results[1];
		double percentReport = (1.0* reportSubmit) / ( 1.0* groundTruth[2] );
		double percentSavedEffort = 1.0 - percentReport;
		
		Double F1 = ( 2.0* percentBug * percentSavedEffort) / (percentBug + percentSavedEffort);
		
		double difWithOptimal = reportSubmit - groundTruth[1];
		
		Double[] performance = { 1.0*bugDetected, 1.0*groundTruth[0], percentBug, 1.0*reportSubmit, 1.0*groundTruth[2], percentReport, percentSavedEffort, F1, 1.0*groundTruth[1], difWithOptimal  };
		
		return performance;
	}
	
	/*
	 * 其他的evaluatePerformance方法都是假定，停止后，方法预测的缺陷数目和已经发现的缺陷数目是一样的；这比较理想化；
	 * 这个方法是基于真实情况下 endReport为止，发现的所有缺陷的数目
	 */	
	public Double[] evaluatePerformanceByEndReport ( Integer endReport, TestProject project ){
		HashSet<String> noDupTag = new HashSet<String>();
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		for ( int i =0; i <= endReport && i < reportList.size(); i++  ) {
			TestReport report = reportList.get(i );
			if ( report.getBugTag().equals( "审核通过") )
				noDupTag.add( report.getDupTag() );
		}
		int bugDetected = noDupTag.size();
		
		for ( int i = endReport+1; i < reportList.size(); i++ ){
			TestReport report = reportList.get(i );
			if ( report.getBugTag().equals( "审核通过") )
				noDupTag.add( report.getDupTag() );
		}
		int totalBugs = noDupTag.size();
		double percentBug = (1.0*bugDetected) / (1.0*totalBugs );
		
		double percentReport = (1.0*endReport) / reportList.size();
		double percentSavedEffort = 1.0-percentReport;
		
		Double F1 = ( 2.0* percentBug * percentSavedEffort) / (percentBug + percentSavedEffort);
		
		int optimalInsNum = reportList.size();
		HashSet<String> insNoDupTag = new HashSet<String>();
		for ( int i = 0; i < reportList.size() ; i++ ){
			TestReport report = reportList.get(i);
			if ( report.getBugTag().equals( "审核通过"))
				continue;
			String dupTag = report.getDupTag();
			if ( !insNoDupTag.contains( dupTag )){
				optimalInsNum = i +1;
				insNoDupTag.add( dupTag );
			}
		}
		double difWithOptimal = endReport - optimalInsNum;
		
		Double[] performance = { 1.0*bugDetected, 1.0*totalBugs, percentBug, 1.0*endReport, 1.0*reportList.size(), percentReport, percentSavedEffort, F1, 1.0*optimalInsNum, difWithOptimal  };
		return performance;
	}
	
	public Double[] evaluatePerformanceDistribution ( Integer[] results, String project ) {  
		Double[] performance = this.evaluatePerformance(results, project);
		
		Double[] extendPerformance = new Double[performance.length + results.length - 2];
		for ( int i =0; i < performance.length; i++ )
			extendPerformance[i] = performance[i];
	
		int length = performance.length;
		for ( int i =2; i < results.length; i++ )
			extendPerformance[length+i-2] = 1.0*results[i];   //from result[2] 
		return extendPerformance; 
	}
	
	public HashMap<String, Integer[]> getGroundTruthMap() {
		return groundTruthMap;
	}
	public void setGroundTruthMap(HashMap<String, Integer[]> groundTruthMap) {
		this.groundTruthMap = groundTruthMap;
	}
}
