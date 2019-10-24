package com.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.data.Constants;
import com.data.TestProject;

public class PerformanceComParisonForCategories {
	String[] methodNames = {"iSENSE2.0", "CRC"};
	HashMap<String, ArrayList<Integer>> categoryMap = new HashMap<String, ArrayList<Integer>>();
	HashMap<Integer, String> projectCatMap = new HashMap<Integer, String>();
	
	public PerformanceComParisonForCategories() {
		// TODO Auto-generated constructor stub
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( "data/input/threeBugTrend-predicted.csv" )));	
			String line = "";
			while ( (line = br.readLine()) != null ){
				String[] temp = line.split( ",");
				Integer projectId = Integer.parseInt( temp[0]);
				String categoryId = temp[1].trim();
				
				projectCatMap.put( projectId, categoryId );
				
				if ( categoryMap.containsKey( categoryId )){
					ArrayList<Integer> projectList = categoryMap.get( categoryId );
					projectList.add( projectId );
					categoryMap.put( categoryId, projectList );
				}else{
					ArrayList<Integer> projectList = new ArrayList<Integer>();
					projectList.add( projectId );
					categoryMap.put( categoryId, projectList );
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/*
	 * combination 和 dynamic 进行对比
	 */
	public void  oragnizePerformanceInCategories ( String folderName ) {
		PerformanceReader perfReader = new PerformanceReader();
		for ( int i = 0; i < methodNames.length; i++ ){
			String methodName = methodNames[i];
			String fileName = folderName + "/" + methodName + ".csv";
			System.out.println( fileName );
			HashMap<Integer, Double[]> performance = perfReader.readPerformanceNoHeaderOnlyPID( fileName);
			
			HashMap<String, HashMap<Integer, Double[]>> performanceCategories = this.obtainPerformanceEachCategory(performance);
			for ( String cat : performanceCategories.keySet() ){
				HashMap<Integer, Double[]> catPerformance = performanceCategories.get( cat );
				this.outputPerformance( "data/output/baseline-dyn/contribution-" + methodNames[i] + "-cat" + cat + ".csv", catPerformance);
			}
		}
	}
	
	public void outputPerformance ( String fileName, HashMap<Integer, Double[]> performance ){
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( fileName )));
			
			for ( Integer projectId : performance.keySet() ){
				Double[] values = performance.get( projectId );
				writer.write( projectId + ",");
				for ( int i =0; i < values.length; i++ ){
					writer.write( values[i] + ",");
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
	
	public HashMap<String, HashMap<Integer, Double[]>> obtainPerformanceEachCategory ( HashMap<Integer, Double[]> performance ){
		HashMap<String, HashMap<Integer, Double[]>> performanceCategories = new HashMap<String, HashMap<Integer, Double[]>>();
		for ( int i =1; i <=3; i++ ){
			HashMap<Integer, Double[]> catPerformance = new HashMap<Integer, Double[]>();
			performanceCategories.put( new Integer(i).toString(), catPerformance );
		}
		
		for ( Integer projectId: performance.keySet() ){
			String cat = projectCatMap.get( projectId );
			HashMap<Integer, Double[]> catPerformance = performanceCategories.get( cat );
			catPerformance.put( projectId, performance.get( projectId ));
			performanceCategories.put( cat, catPerformance );
		}
		
		return performanceCategories;
	}
	
	public static void main ( String[] args ){
		PerformanceComParisonForCategories perfComp = new PerformanceComParisonForCategories();
		perfComp.oragnizePerformanceInCategories( "data/output/baseline-dyn");
	}
}
