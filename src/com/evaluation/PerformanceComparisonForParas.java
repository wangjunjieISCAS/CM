package com.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.data.Constants;

public class PerformanceComparisonForParas {

	public PerformanceComparisonForParas() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * 将所有的参数下，方法的结果（median或average）放在一个文件夹里面
	 */
	public void ouputPerformanceComparison ( String folderName ){
		HashMap<String, Double[]> statisMedianValues = new HashMap<String, Double[]>();
		HashMap<String, Integer> riskValues = new HashMap<String, Integer>();
		
		PerformanceReader reader = new PerformanceReader ();
		File projectFolder = new File ( folderName );
		if ( projectFolder.isDirectory() ) {
			String[] projectFileList = projectFolder.list();
			for ( int i =0; i < projectFileList.length; i++ ) {
				String fileName = projectFileList[i];
				String fileNameCont = fileName.replace(".csv", "");
				String[] temp = fileNameCont.split( "-");
				String para1 = temp[2];
				String para2 = temp[3];
				
				HashMap<String, Double[]> performanceUnderPara = reader.readPerformanceAllProjectsUnderSpefPara(folderName + "/"+ fileName, Constants.attrName );
				Double[] medianValues = this.computeMedianValues( performanceUnderPara );
				Integer riskValue = this.computeRiskValues( performanceUnderPara );
				
				statisMedianValues.put( para1+"-"+para2, medianValues );
				riskValues.put( para1+"-"+para2, riskValue );
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File ( "data/output/performanceLightPred/statisMedianValues.csv")));
			writer.write( "stableThres" +"," + "simThres" + ",");
			for ( int i=0; i < Constants.attrName.length; i++ ){
				writer.write( Constants.displayAttrName[i] + ",");
			}
			writer.write( "riskValue");
			writer.newLine();
			for ( String para: statisMedianValues.keySet() ){
				Double[] medianValues = statisMedianValues.get( para );
				Integer riskValue = riskValues.get( para );
				
				String[] paraList = para.split( "-");
				writer.write( paraList[0] + "," + paraList[1] + "," );
				for ( int i=0; i < Constants.attrName.length; i++ ){
					writer.write( medianValues[i] + ",");
				}
				writer.write( riskValue.toString() );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Double[] computeMedianValues ( HashMap<String, Double[]> performance){
		Double[] statisValues = new Double[Constants.attrName.length];
		for ( int i =0; i < Constants.attrName.length; i++ ){
			ArrayList<Double> attrValueList = new ArrayList<Double>();
			
			for ( String projectName : performance.keySet() ){
				Double[] attrValues = performance.get( projectName );
				attrValueList.add( attrValues[i] );
			}
			
			Collections.sort( attrValueList );
			
			int middle = attrValueList.size()/2;
			Double median = attrValueList.get( middle );
			statisValues[i] = median;
		}
		return statisValues;
	}
	
	//%bug小于80%的算risk
	public Integer computeRiskValues ( HashMap<String, Double[]> performance ){
		Integer riskValue = 0;
		for ( String projectName : performance.keySet() ){
			Double[] attrValues = performance.get( projectName );
			Double pertBug = attrValues[0];
			if ( pertBug < 0.8 )
				riskValue++;
		}
		return riskValue;
	}
	
	public static void main ( String[] args ){
		PerformanceComparisonForParas perfTool = new PerformanceComparisonForParas();
		perfTool.ouputPerformanceComparison( "data/output/performanceLightPred/NJCRCM0");
	}
}
