package com.RBPredictionCoverage;

import java.util.ArrayList;
import java.util.HashMap;

public class CoverageMeasurement {
	public Double measureCurrentCoverage ( ArrayList<String> taskTerms, ArrayList<HashMap<String, Integer>> reportTermsList ){
		ArrayList<String> reportTerms = new ArrayList<String>();
		for ( int i =0; i < reportTermsList.size(); i++ ){
			HashMap<String, Integer> terms = reportTermsList.get( i );
			reportTerms.addAll( terms.keySet() );
		}
		
		int count = 0;
		for ( int i =0; i < taskTerms.size(); i++ ){
			String term = taskTerms.get(i);
			if ( reportTerms.contains( term )){
				count++;
			}
		}
		
		double ratio = 1.0*count / taskTerms.size(); 
		return ratio;
	}
}
