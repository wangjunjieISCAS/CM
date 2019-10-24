package com.evaluation;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.data.Constants;

public class PlotTool {

	public PlotTool() {
		// TODO Auto-generated constructor stub
	}
	
	//输出和baseline对比的boxPlot
	public void compareWithBaseline ( String folderName, String[] methodNames, String outFile, String[] displayMethodNames ){
		HashMap<String, HashMap<Integer, Double[]>> totalPerformance = new HashMap<String, HashMap<Integer, Double[]>>();
		//<methodName, <projectId, <performance>>
		PerformanceReader perfReader = new PerformanceReader();
		for ( int i = 0; i < methodNames.length; i++ ){
			String methodName = methodNames[i];
			String fileName = folderName + "/" + methodName + ".csv";
			System.out.println( fileName );
			HashMap<Integer, Double[]> performance = perfReader.readPerformanceNoHeaderOnlyPID( fileName);
			
			totalPerformance.put( methodName, performance );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File ( outFile )));
			writer.write( " " + "," + "  " + "," + "values");
			writer.newLine();
			for ( int k= 0; k < methodNames.length; k++ ){
				String methodName = methodNames[k];
				HashMap<Integer, Double[]> performance = totalPerformance.get( methodName );
				for ( Integer projectId: performance.keySet() ){
					Double[] values = performance.get( projectId );
					for ( int i =0; i < values.length; i++ ){
						writer.write( displayMethodNames[k] + "," + Constants.displayAttrName[i] + "," + values[i]);
						writer.newLine();
					}
				}				
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main ( String[] args ){
		PlotTool tool = new PlotTool();
	
		String[] methodNames = {  "CRC", "iSENSE2.0", "contribution-CRC-cat1", "contribution-iSENSE2.0-cat1",  
				"contribution-CRC-cat2" ,  "contribution-iSENSE2.0-cat2", "contribution-CRC-cat3", "contribution-iSENSE2.0-cat3"};
		String[] displayMethodNames = {  "CRC", "iSE", "CRC-1", "iSE-1", "CRC-2", "iSE-2", "CRC-3", "iSE-3" };  //"CRC", "iSE2.0",
		tool.compareWithBaseline("data/output/baseline-dyn", methodNames, "data/output/baseline-dyn/effective-dyn.csv", displayMethodNames );
		
		/*
		String[] methodNames = { "iSENSE2.0", "iSENSE", "Rayleigh"}; 
		tool.compareWithBaseline("data/output/baseline", methodNames, "data/output/baseline/effective.csv", methodNames );
		*/
		
		/*
		 //CRC for iSENSE2.0
		String prefix = "performance-cmb";
		String lastfix = "CRC-underBestPara";
		String[] methodNames = { prefix+ "M0"+lastfix, prefix+ "MhCH"+lastfix, 
				prefix+ "MhJK"+lastfix, prefix+ "Mth"+lastfix, prefix+ "MtCH"+lastfix }; 
		String[] displayMethodNames = {  "M0", "Mth", "MhJK", "MhCH", "MtCH" };
		tool.compareWithBaseline( "data/output/performanceLightPred", methodNames, "data/output/performanceLightPred/CRCCompare.csv", displayMethodNames );
		*/
		
		/*
		 * CRC for iSENSE 1.0
		String prefix = "performance-iSENSE-";
		String[] methodNames = { prefix+ "M0", prefix+ "Mth", 
				prefix+ "MhJK", prefix+ "MhCH", prefix+ "MtCH"}; 
		String[] displayMethodNames = {  "M0", "Mth", "MhJK", "MhCH", "MtCH" };
		tool.compareWithBaseline( "data/output/performanceiSENSE", methodNames, "data/output/performanceiSENSE/CRCCompare1.0.csv", displayMethodNames );
				 */
	}
}
