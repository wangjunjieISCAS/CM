package com.baselineiSENSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.data.TestReport;

/*
 * Estimating Animal Abundance with Capture Frequency Data
 * N = S + f1*f1 / (2*f2)
 */
public class MhCHCRCAlgorithm extends CRCAlgorithm{

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
		
		Double NValue = 0.0;
		if (numCapturesize*numForEachFreq.get(1) > 2*numForEachFreq.get(2)  && numCapturesize * numForEachFreq.get(2) > 3*numForEachFreq.get(3) 
				&& 3*numForEachFreq.get(1)*numForEachFreq.get(3) > 2*numForEachFreq.get(2)*numForEachFreq.get(2)) {
			NValue = numDistinctBugs +  ( (1.0*numForEachFreq.get(1)*numForEachFreq.get(1)) / (2.0*numForEachFreq.get(2)) ) * ( 1.0- 2.0*numForEachFreq.get(2) /
					(numCapturesize*numForEachFreq.get(1)) ) / ( 1.0- 3.0*numForEachFreq.get(3)/ (numCapturesize * numForEachFreq.get(2)));
			System.out.println( "The refined NValue is : " + NValue);
		}else {
			if ( numForEachFreq.get( 2) == 0 )
				numForEachFreq.put (2, 1);
			NValue = numDistinctBugs + (1.0*numForEachFreq.get( 1) * numForEachFreq.get( 1)) / (2.0* numForEachFreq.get(2));
			System.out.println( "The default NValue is : "  + NValue );
		}
		int N = NValue.intValue();
		
		Integer[] results = {N, N, N, numDistinctBugs.intValue() };
		return results;
	}

	public static void main ( String args[] ) {
		MhCHCRCAlgorithm mth = new MhCHCRCAlgorithm();
		
		TreeMap<Integer, ArrayList<TestReport>> captureProcess = new TreeMap<Integer, ArrayList<TestReport>>();
		mth.obtainRecaptureResults(captureProcess);
	}
}
