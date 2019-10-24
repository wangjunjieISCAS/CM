package com.baselineiSENSEEvaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import com.data.Constants;


public class TemporalEvaluation {
	public void conductTemporalEvaluation ( String folderName , String outFile ) {
		PerformanceReader perfReader = new PerformanceReader();
		HashMap<Integer, HashMap<Integer, Double[]>> attrValuesList = perfReader.readPerformanceAllParameters(folderName, Constants.attrName, 1000);
		
		BestParameterForTaskSubset findBestReader = new BestParameterForTaskSubset();
		
		ArrayList<Integer> projectList = new ArrayList<Integer>();
		HashMap<Integer, Double[]> attrValues = new HashMap<Integer, Double[]>();
		for ( Integer para : attrValuesList.keySet() ) {
			attrValues = attrValuesList.get( para);
			break;
		}
		for ( Integer projectId : attrValues.keySet() ) {
			projectList.add( projectId );
		}
		Collections.sort( projectList );
		
		TreeMap<Integer, Double[]> performanceList = new TreeMap<Integer, Double[]>();
		TreeMap<Integer, Integer> bestParaList = new TreeMap<Integer, Integer>();
		for ( int i = Constants.EVALUATION_TIME_SERIES_BEGIN; i < projectList.size(); i++ ) {
			//train set is projectList(0) - projectList(beginTaskId-1)
			Integer bestPara = findBestReader.computeBestParameterForTaskSubset(folderName, projectList.get(i-1) );
			
			Double[] performance = perfReader.readPerformanceSpecificParameterSpecificProject(folderName, Constants.attrName, bestPara, projectList.get(i) );
			
			performanceList.put( projectList.get(i), performance );
			bestParaList.put( projectList.get( i), bestPara );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File( outFile )));
			for ( Integer projectId : performanceList.keySet() ) {
				writer.write( projectId + "," );
				Double[] performance = performanceList.get( projectId );
				for ( int i=0; i < performance.length; i++  ) {
					writer.write( performance[i] + ",");
				}
				writer.write( "," + "," + bestParaList.get( projectId ) );
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
		TemporalEvaluation evaluation = new TemporalEvaluation();
		String[] CRCTypeList = { "M0", "Mth", "MhJK", "MhCH", "MtCH"};
		for ( int i =0; i < CRCTypeList.length; i++ ){
			String CRCType = CRCTypeList[i];
			String folderName = "data/output/performanceiSENSE/" + CRCType;
			String outFile = "data/output/performanceiSENSE/performance-iSENSE-" + CRCType + ".csv";
			evaluation.conductTemporalEvaluation(folderName, outFile);
		}
		
	}
}
