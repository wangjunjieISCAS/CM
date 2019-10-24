package com.bugTrendClassify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class ThreeBugTrendClassify {
	Integer stableNumberThres = 20;
	Double bugPercentageThres = 0.8;
	
	public void classifyThreeBugTrend ( String bugNumberFolder ) {
		TreeMap<Integer, Integer> projectTypeList = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Double> bugPercList = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> idealStopPointList = new TreeMap<Integer, Double>();
		
		TreeMap<Integer, TreeMap<Integer, Integer>> bugNumberSum = this.readBugNumberDataForAllProjects( bugNumberFolder );
		for ( Integer projectId : bugNumberSum.keySet() ) {
			TreeMap<Integer, Integer> bugNumberList = bugNumberSum.get( projectId );
			Object[] result = this.classifyBugTrend( bugNumberList );
			Integer type = (Integer) result[0];
			Double bugPercentage = (Double) result[1];
			Double idealStopPoint = (Double) result[2];
			
			projectTypeList.put( projectId, type );
			bugPercList.put( projectId, bugPercentage );
			idealStopPointList.put( projectId, idealStopPoint );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File ( "data/input/threeBugTrend.csv")));
			for ( Integer projectId : projectTypeList.keySet() ) {
				writer.write( projectId + "," + projectTypeList.get( projectId) + "," + bugPercList.get( projectId ) + "," + idealStopPointList.get( projectId ));
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object[] classifyBugTrend ( TreeMap<Integer, Integer> bugNumberList ) {
		int stableNumber = 0;
		int bugNumBeforeStable = 0, totalBugNum=0;
		int idealStopPoint = 0;
		boolean isStable = false;
		int totalReportNum = 0;
		for ( Integer reportNum : bugNumberList.keySet() ) {
			Integer bugNum = bugNumberList.get( reportNum );
			totalBugNum += bugNum;
			
			if ( isStable == false ) {
				bugNumBeforeStable += bugNum;
				if ( bugNum == 0 ) {
					stableNumber ++;
					idealStopPoint = reportNum;
				}
				else {
					stableNumber = 0;
				}
			}
			
			if ( stableNumber > stableNumberThres && totalBugNum != 0 ) {
				isStable = true;
			}
			totalReportNum = reportNum;
		}
		
		Integer type = 0;
		double bugPercentage = (1.0*bugNumBeforeStable ) / totalBugNum ;
		if ( bugPercentage <=  bugPercentageThres ) {
			//type = 2;
			type = 3;
		}else if ( bugNumBeforeStable == totalBugNum ) {
			type = 1;
		}else {
			type =2 ;   //type = 3;
		}
		
		Object[] result = {type, bugPercentage, (1.0*idealStopPoint)/totalReportNum  };
		return result;
	}
	
	public TreeMap<Integer, TreeMap<Integer, Integer>> readBugNumberDataForAllProjects ( String folderName ) {
		TreeMap<Integer, TreeMap<Integer, Integer>> bugNumberSum = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		
		File folder = new File ( folderName );
		if ( folder.isDirectory() ) {
			String[] files = folder.list();
			for ( int i =0; i < files.length; i++ ) {
				String fileName = folderName + "/" + files[i];
				
				TreeMap<Integer, Integer> bugNumberList = this.readBugNumberData( fileName );
				
				String temp = files[i].substring( 0, files[i].length() - new String(".csv").length() );
				Integer projectId = Integer.parseInt( temp);
				bugNumberSum.put( projectId, bugNumberList );
			}
		}
		return bugNumberSum;
	}
		
	public TreeMap<Integer, Integer> readBugNumberData ( String fileName ) {
		TreeMap<Integer, Integer> bugNumberList = new TreeMap<Integer, Integer>();
		
		try {
			BufferedReader reader = new BufferedReader ( new FileReader( new File ( fileName ) ));
			String line = "";
			while ( (line = reader.readLine()) != null ) {
				String[] temp = line.split( ",");
				Integer id = Integer.parseInt( temp[0] );
				Integer bugNum = Integer.parseInt( temp[1] );
				bugNumberList.put( id, bugNum );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bugNumberList;
	}
	
	public static void main ( String[] args ) {
		ThreeBugTrendClassify bugTrend = new ThreeBugTrendClassify();
		
		String bugNumberFolder = "data/input/bugData-Predicted";
		bugTrend.classifyThreeBugTrend( bugNumberFolder ); 
	}
}
