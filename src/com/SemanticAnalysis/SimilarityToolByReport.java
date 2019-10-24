package com.SemanticAnalysis;

import java.util.ArrayList;
import java.util.HashMap;


public class SimilarityToolByReport {
	
	public Integer similarityBasedWordEmbedding ( HashMap<String, Integer> curReportTerms, ArrayList<HashMap<String, Integer>> histReportTermsList, Double simThres,
			HashMap<String, ArrayList<Double>> termEmbList ){
		ArrayList<Double> curReportVec = this.sentenceToVecNotConsiderFreq(termEmbList, curReportTerms );
		
		double maxSimValue = 0.0;
		Integer maxTag = 0;
		for ( int i =0; i < histReportTermsList.size(); i++ ){
			HashMap<String, Integer> histReportTerms = histReportTermsList.get( i );
			ArrayList<Double> histReportVec = this.sentenceToVecNotConsiderFreq(termEmbList, histReportTerms );
			
			Double simValue = this.cosineSimilarityForList( histReportVec, curReportVec );
			
			if ( simValue > maxSimValue ){
				maxSimValue = simValue;
				maxTag = i;
			}
		}
		
		if ( maxSimValue < simThres ) {
			maxTag = -1;
		}

		return maxTag;		
	}
	
	
	public ArrayList<Double> sentenceToVecNotConsiderFreq ( HashMap<String, ArrayList<Double>> termEmbList, HashMap<String, Integer> reportTerms ){
		ArrayList<Double> sentVecValue = new ArrayList<Double>();
		
		for ( String term : reportTerms.keySet() ){
			if ( !termEmbList.containsKey( term ))
				continue;
			ArrayList<Double> vecValue = termEmbList.get( term );
			sentVecValue = this.arraylistAdd(sentVecValue, vecValue );
		}
		return sentVecValue;
	}
	
	public ArrayList<Double> arraylistAdd ( ArrayList<Double> vecValue1, ArrayList<Double> vecValue2 ){
		if ( vecValue1.size() == 0 )
			return vecValue2;
		if ( vecValue2.size() == 0 )
			return vecValue2;
		
		ArrayList<Double> vecValueAll = new ArrayList<Double>();
		for ( int i =0; i < vecValue1.size(); i++ ){
			double value = vecValue1.get(i) + vecValue2.get( i );
			vecValueAll.add( value );
		}
		return vecValueAll;
	}
	
	public Double cosineSimilarityForList ( ArrayList<Double> vecValue1, ArrayList<Double> vecValue2 ){
		double downValue1 = 0.0, downValue2 = 0.0, upValue = 0.0;
		for ( int i =0; i < vecValue1.size(); i++ ){
			double value1 = vecValue1.get( i );
			double value2 = vecValue2.get( i );
			
			downValue1 += value1 * value1;
			downValue2 += value2 * value2;
			
			upValue += value1 * value2;
		}
		
		downValue1 = Math.sqrt( downValue1 );
		downValue2 = Math.sqrt( downValue2 );
		Double simValue = upValue / (downValue1 * downValue2 );
		return simValue;
	}
}
