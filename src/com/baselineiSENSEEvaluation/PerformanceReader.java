package com.baselineiSENSEEvaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.csvreader.CsvReader;
import com.data.Constants;

public class PerformanceReader {
	public HashMap<Integer, Double[]> readPerformance ( String fileName , String[] attrName, int maxTaskId ) {
		HashMap<Integer, Double[]> attrValues = new HashMap<Integer, Double[]>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));	
			CsvReader reader = new CsvReader( br, ',');
			
			reader.readHeaders(); 
	        while ( reader.readRecord() ){
        		Double[] values = new Double[attrName.length];
        		String projectName = reader.get( "project");
        		String[] temp = projectName.split( "-");
        		Integer projectId = Integer.parseInt( temp[0] );
        		if ( projectId > maxTaskId )
        			continue;
        		
	        	for ( int i =0; i < attrName.length; i++ ) {
	        		Double value = Double.parseDouble( reader.get( attrName[i] ) ) ;        
	        		values[i] = value;
	        	}
	        	attrValues.put( projectId, values );
	        }
			
	        reader.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return attrValues;
	}
	
	public Double[] readPerformanceSpecificProject ( String fileName, String[] attrName, int taskId ) {
		Double[] projectPerformance = new Double[Constants.attrName.length];
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));	
			CsvReader reader = new CsvReader( br, ',');
			
			reader.readHeaders(); 
	        while ( reader.readRecord() ){
        		String projectName = reader.get( "project");
        		String[] temp = projectName.split( "-");
        		Integer projectId = Integer.parseInt( temp[0] );
        		if ( projectId == taskId ) {
        			for ( int i =0; i < attrName.length; i++ ) {
    	        		Double value = Double.parseDouble( reader.get( attrName[i] ) ) ;        
    	        		projectPerformance[i] = value;
    	        	}
        			break;
        		}
	        }
			
	        reader.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectPerformance;
	}
	
	public Double[] readPerformanceSpecificParameterSpecificProject ( String folderName, String[] attrName, int bestPara, int taskId  ) {
		Double[] projectPerformance = null;
		File projectFolder = new File ( folderName );
		if ( projectFolder.isDirectory() ) {
			String[] projectFileList = projectFolder.list();
			for ( int i =0; i < projectFileList.length; i++ ) {
				String fileName = projectFileList[i];
				String shortFileName = fileName.replace(".csv", "");
				String[] temp = shortFileName.split( "-");
				Integer para = Integer.parseInt( temp[1] );
				
				if ( para == bestPara) {
					projectPerformance = this.readPerformanceSpecificProject( folderName + "/" + fileName, attrName, taskId);
					break;
				}
			}
		}
		return projectPerformance;
	}
	
	
	public HashMap<Integer, HashMap<Integer, Double[]>> readPerformanceAllParameters ( String folderName, String[] attrName, int maxTaskId ) {
		HashMap<Integer, HashMap<Integer, Double[]>> attrValuesParas = new HashMap<Integer, HashMap<Integer, Double[]>>();
		File projectFolder = new File ( folderName );
		if ( projectFolder.isDirectory() ) {
			String[] projectFileList = projectFolder.list();
			for ( int i =0; i < projectFileList.length; i++ ) {
				String fileName = projectFileList[i];
				
				String shortFileName = fileName.replace(".csv", "");
				String[] temp = shortFileName.split( "-");
				Integer para = Integer.parseInt( temp[1] );
				
				//all the performance values for the projects whose projectId is smaller than or equal with maxTaskId, under specific parameter para
				HashMap<Integer, Double[]> attrValues = this.readPerformance( folderName+ "/" +fileName, attrName, maxTaskId );
				
				attrValuesParas.put( para, attrValues );
			}
		}
		return attrValuesParas;
	}
}
