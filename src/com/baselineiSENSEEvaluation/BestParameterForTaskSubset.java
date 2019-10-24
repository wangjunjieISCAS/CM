package com.baselineiSENSEEvaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import com.data.Constants;



public class BestParameterForTaskSubset {
	
	public Integer computeBestParameterForTaskSubset ( String folderName, int maxTaskId ) {
		PerformanceReader perfReader = new PerformanceReader();
		HashMap<Integer, HashMap<Integer, Double[]>> attrValuesList = perfReader.readPerformanceAllParameters(folderName, Constants.attrName, maxTaskId);
		//<para, <projectId, performance>>
		
		HashMap<Integer, Double[]> medianValuesPara = new HashMap<Integer, Double[]>();
		for ( Integer para:  attrValuesList.keySet() ) {
			HashMap<Integer, Double[]> attrValues = attrValuesList.get( para );
			Double[] medianAttrValues = this.obtainMedianPerformance(attrValues);
			
			medianValuesPara.put( para, medianAttrValues );
		}
				
		//parameter, number
		Integer optPara = this.findMaxF1( medianValuesPara );
		return optPara;				
	}	
	
	public Double[] obtainMedianPerformance ( HashMap<Integer, Double[]> attrValues ) {
		TreeMap<Integer, ArrayList<Double>> newAttrValues = new TreeMap<Integer, ArrayList<Double>>();   //id is the index of the attr
		for ( Integer projectId : attrValues.keySet() ) {
			Double[] values = attrValues.get( projectId );
			for ( int  i=0; i <values.length; i++ ) {
				
				ArrayList<Double> newValues = new ArrayList<Double>();
				if ( newAttrValues.containsKey( i ) ) {
					newValues = newAttrValues.get( i );
				}
				newValues.add( values[i] );
				newAttrValues.put( i, newValues );
			}
		}
		
		Double[] medianAttrValues = new Double[Constants.attrName.length];
		int index = 0;
		for ( Integer attr : newAttrValues.keySet() ) {
			ArrayList<Double> newValues = newAttrValues.get( attr );
			
			Collections.sort( newValues );
			double median = 0.0;
			int midIndex = newValues.size() / 2;
			if ( newValues.size() % 2 == 1 ) {
				median = newValues.get( midIndex );
			}
			else {
				median = ( newValues.get( midIndex -1) + newValues.get(midIndex)) / 2;
			}
			
			medianAttrValues[index++] = median;
		}		
		return medianAttrValues;
	}
	
	
	//bugs > 0.99, report > 0.2, maximize F1
	public Integer findMaxF1 ( HashMap<Integer, Double[]> performance ) {
		double maxF1 = -1.0;
		int optPara = -1;
		double maxBug = 0.0;   //修改了逻辑，先找到最大的bug%，然后满足最大bug%的 最大F1
		for ( Integer para : performance.keySet() ){
			Double[] value = performance.get( para );
			if ( value[0] > maxBug ){
				maxBug = value[0];
			}
		}
				
		for ( Integer para : performance.keySet() ) {
			Double[] value = performance.get( para );
			if ( value[0] == maxBug && value[2] >= maxF1 ) {
				maxF1 = value[2];
				optPara = para;
			}
		}
		
		return optPara;
	}	
}
