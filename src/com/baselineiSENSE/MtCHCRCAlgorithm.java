package com.baselineiSENSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.data.TestReport;

/*
 * fi: number of animals captured exactly i times in the t samples
 * zi: number of animals captured only in the ith sample, sum Zi = f1
 * Estimating Population Size for Sparse Data in Capture-Recapture Experiments, Mt part
 * N = S + sum(i=1-t)sum(j=i+1-t)ZiZj / (f2+1)
 */
public class MtCHCRCAlgorithm extends CRCAlgorithm{
	@Override
	public Integer[] obtainRecaptureResults(TreeMap<Integer, ArrayList<TestReport>> captureProcess) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> numForEachFreq = new HashMap<Integer, Integer>();
		TreeMap<Integer, Integer> numOnlyInISample = new TreeMap<Integer, Integer>();
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
		int index = 1;
		int confTest =0;
		for ( Integer cap: captureProcess.keySet() ) {
			ArrayList<TestReport> reportList = captureProcess.get( cap );
			
			int onlyCount = 0;
			for ( int i =0; i < reportList.size(); i++ ) {
				String dupTag = reportList.get(i).getDupTag();
				int count = dupNum.get( dupTag );
				if ( count == 1 ) {
					onlyCount++;					
				}
			}
			numOnlyInISample.put( index++ , onlyCount);
			confTest += onlyCount;
		}
		if ( numForEachFreq.containsKey( 1) && confTest != numForEachFreq.get(1 )) {
			System.out.println( "Confidence test! Sum Zi not equal with F1");
		}
		
		int numDistinctBugs = distinctBugs.size();
		
		if ( !numForEachFreq.containsKey(2)) {
			numForEachFreq.put(2, 0);
		}
		
		int sumZ = 0;
		for ( Integer sample : numOnlyInISample.keySet()  ) {
			for ( Integer innSample: numOnlyInISample.keySet() ) {
				if ( innSample <= sample )
					continue;
				sumZ += numOnlyInISample.get( sample ) * numOnlyInISample.get( innSample );
			}			
		}
		
		Double NValue = 1.0*numDistinctBugs + 1.0* sumZ / ( numForEachFreq.get(2) + 1 );
		int N = this.DoubleFormatInt( NValue );
		
		Integer[] results = {N, N, N, numDistinctBugs };
		return results;
	}

	
	public static void main ( String args[] ) {
		MtCHCRCAlgorithm mth = new MtCHCRCAlgorithm();
		
		TreeMap<Integer, ArrayList<TestReport>> captureProcess = new TreeMap<Integer, ArrayList<TestReport>>();
		mth.obtainRecaptureResults(captureProcess);
	}
}
