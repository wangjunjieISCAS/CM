package com.baselineiSENSE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.data.TestReport;

/*
 * Estimation of the Size of a Closed Population when Capture Probabilities vary Among animals
 */
public class MhJKCRCAlgorithm extends CRCAlgorithm{
	@Override
	public Integer[] obtainRecaptureResults(TreeMap<Integer, ArrayList<TestReport>> captureProcess) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> numForEachFreq = new HashMap<Integer, Integer>();
		HashSet<String> distinctBugs = new HashSet<String>();
		
		HashMap<String, Integer> dupNum = new HashMap<String, Integer>();
		for ( Integer cap : captureProcess.keySet() ) {
			ArrayList<TestReport> reportList = captureProcess.get( cap );
		
			for ( int i =0; i < reportList.size(); i++ ) {
				String dupTag = reportList.get(i).getDupTag();
				int count = 1;
				if ( dupNum.containsKey( dupTag ))
					count += dupNum.get( dupTag );
				dupNum.put( dupTag, count );
				
				distinctBugs.add( dupTag );
			}
		}
		for ( String dupTag : dupNum.keySet() ) {
			Integer num = dupNum.get( dupTag );
			int count = 1;
			if ( numForEachFreq.containsKey( num )) {
				count += numForEachFreq.get( num );
			}
			numForEachFreq.put( num, count );
		}
		Double numDistinctBugs = 1.0*distinctBugs.size();
		Double numCapturesize = 1.0*captureProcess.size();   //t
		
		//for test 
		/*
		Integer[] freq = { 43, 16, 8, 6, 0, 2, 1};
		for ( int i =0; i < freq.length; i++ ) {
			numForEachFreq.put( i+1, freq[i] );
		}
		numDistinctBugs = 76.0;
		numCapturesize = 18.0;
*/
		if ( !numForEachFreq.containsKey(1)) {
			numForEachFreq.put(1, 0);
		}
		if ( !numForEachFreq.containsKey(2)) {
			numForEachFreq.put(2, 0);
		}
		if ( !numForEachFreq.containsKey(3)) {
			numForEachFreq.put(3, 0);
		}
		Double NJ1Value = numDistinctBugs + ( ( numCapturesize -1) / numCapturesize ) * numForEachFreq.get( 1);
		System.out.println( "NJ1: " + NJ1Value );
		Integer NJ1 = this.DoubleFormatInt( NJ1Value );
		
		Double NJ2Value = numDistinctBugs + ((2*numCapturesize-3)/numCapturesize) * numForEachFreq.get(1) - 
				( ((numCapturesize-2)*(numCapturesize-2)) / (numCapturesize*(numCapturesize-1)))* numForEachFreq.get(2);
		System.out.println( "NJ2: " + NJ2Value );
		Integer NJ2 = this.DoubleFormatInt( NJ2Value );
		
		Double NJ3Value = numDistinctBugs + ((3*numCapturesize-6)/numCapturesize)* numForEachFreq.get(1) - 
				( (3*numCapturesize*numCapturesize - 15*numCapturesize + 19) / (numCapturesize*(numCapturesize-1))) * numForEachFreq.get(2) + 
				( ((numCapturesize-3)*(numCapturesize-3)*(numCapturesize-3)) / ( numCapturesize* (numCapturesize-1)*(numCapturesize-2))) * numForEachFreq.get(3);
		System.out.println( "NJ3: " + NJ3Value );			
		Integer NJ3 = this.DoubleFormatInt( NJ3Value );
		
		Integer[] results = {NJ1, NJ2, NJ3, numDistinctBugs.intValue() };
		return results;
	}
	
	
	public static void main ( String args[] ) {
		MhJKCRCAlgorithm mth = new MhJKCRCAlgorithm();
		
		TreeMap<Integer, ArrayList<TestReport>> captureProcess = new TreeMap<Integer, ArrayList<TestReport>>();
		mth.obtainRecaptureResults(captureProcess);
	}
}
