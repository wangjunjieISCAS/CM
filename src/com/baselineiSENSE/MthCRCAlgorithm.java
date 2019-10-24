package com.baselineiSENSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.data.TestReport;

public class MthCRCAlgorithm extends CRCAlgorithm{
	@Override
	public Integer[] obtainRecaptureResults(TreeMap<Integer, ArrayList<TestReport>> captureProcess) {
		// TODO Auto-generated method stub
		ArrayList<Integer> capNumInEachSample = new ArrayList<Integer>();   
		//ArrayList<Integer> bugNumInEachSample = new ArrayList<Integer>();   //new added
		HashMap<Integer, Integer> numForEachFreq = new HashMap<Integer, Integer>();
		HashSet<String> distinctBugs = new HashSet<String>();
		
		Integer totalNumBugs = 0;
		int zeroSizeNum = 0;
		HashMap<String, Integer> dupNum = new HashMap<String, Integer>();
		for ( Integer cap : captureProcess.keySet() ) {
			ArrayList<TestReport> reportList = captureProcess.get( cap );
			capNumInEachSample.add( reportList.size() );
			if (reportList.size() != 0 )
				zeroSizeNum++;
			
			for ( int i =0; i < reportList.size(); i++ ) {
				String dupTag = reportList.get(i).getDupTag();
				int count = 1;
				if ( dupNum.containsKey( dupTag ))
					count += dupNum.get( dupTag );
				dupNum.put( dupTag, count );
				
				distinctBugs.add( dupTag );
				
				totalNumBugs ++;
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
		Integer numDistinctBugs = distinctBugs.size();
		Integer numCapturesize = captureProcess.size();
		
		//only for demonstration
		/*
		Integer[][] CRCLookupTable = new Integer[numDistinctBugs][numCapturesize];
		for (int i =0; i < CRCLookupTable.length; i++ ) {
			for ( int j =0; j < CRCLookupTable[i].length; j++ ) {
				CRCLookupTable[i][j] = 0;
			}
		}
		ArrayList<String> disBugsTag = new ArrayList<String>();
		for ( Integer cap : captureProcess.keySet() ) {
			ArrayList<TestReport> reportList = captureProcess.get( cap );
			capNumInEachSample.add( reportList.size() );
			
			for ( int i =0; i < reportList.size(); i++ ) {
				String dupTag = reportList.get(i).getDupTag();
				if ( !disBugsTag.contains( dupTag )) {
					disBugsTag.add( dupTag );
				}
				
				int index = disBugsTag.indexOf( dupTag );
				CRCLookupTable[index][cap] = 1;
			}
		}
		for (int i =0; i < CRCLookupTable[0].length; i++ ) {
			System.out.print ( "=========================================");
			for ( int j =0; j < CRCLookupTable.length; j++ ) {
				System.out.print ( CRCLookupTable[j][i] + " ");
			}
			System.out.println( );
		}
		//for test 
		
		capNumInEachSample.clear();
		Integer[] data =  { 3, 2, 2, 5, 6, 4 };
		for ( int i =0; i < data.length; i++) {
			capNumInEachSample.add( data[i] );
		}
		Integer[] freq = { 7, 2, 2, 0, 1, 0 };
		for ( int i =0; i < freq.length; i++ ) {
			numForEachFreq.put( i+1, freq[i] );
		}
		numDistinctBugs = 12;
		numCapturesize = 6;
		
		System.out.println( "********** numDistrictBugs: " + numDistinctBugs );
		System.out.println ( "********** numCapturesize: " + numCapturesize );
		System.out.println( "********** capNumInEachSample: " + capNumInEachSample.toString() );
		//System.out.println( "dupNum: " + dupNum.toString() );
		System.out.println(  "********** numForEachFreq: " + numForEachFreq.toString() );
		*/
		Integer iFiSum = 0;
		for ( Integer num : numForEachFreq.keySet() ) {
			Integer value = numForEachFreq.get( num );
			iFiSum += num * value;
		}
		if (  zeroSizeNum < 2 || numForEachFreq.size() < 2 ) {
			Integer[] results = {0, 0, 0, numDistinctBugs };
			return results;
		}
		if ( !numForEachFreq.containsKey(1)) {
			numForEachFreq.put(1, 0);
		}
		if ( !numForEachFreq.containsKey(2)) {
			numForEachFreq.put(2, 0);
		}
		if ( !numForEachFreq.containsKey(3)) {
			numForEachFreq.put(3, 0);
		}
		
		if ( !iFiSum.equals( totalNumBugs ) ) {
			System.out.println( "confidence checking!! " + iFiSum + " " + totalNumBugs );
		}
		
		Double C1 = 1.0 - ( 1.0*numForEachFreq.get(1)) / (1.0* iFiSum);
		Double C2 = 1.0 - (1.0* numForEachFreq.get(1) - 2.0*numForEachFreq.get(2)/(numCapturesize-1)) / (1.0*iFiSum );
		Double C3 = 1.0 - (1.0*numForEachFreq.get(1) - 2.0*numForEachFreq.get(2)/(numCapturesize-1) + 
				6.0*numForEachFreq.get(3)/ ((numCapturesize-1)*(numCapturesize-2)) ) / (1.0*iFiSum );
		if ( C3 == 0 || C3.equals( Double.NaN))
			C3 = 0.1;
		//System.out.println ( "---------------- C " +  C1 + " " + C2 + " " + C3 );
		
		Integer N01 = (int) ( numDistinctBugs / C1);
		Integer N02 = (int) (numDistinctBugs / C2);
		Integer N03 = (int) (numDistinctBugs / C3);
		//System.out.println ( "---------------- N " + N01+ " " + N02 + " " + N03);
		
		Integer ii1Fi = 0;
		for ( Integer num: numForEachFreq.keySet() ) {
			ii1Fi += (num)*(num-1)*numForEachFreq.get( num);
		}
		Integer njnk = 0;
		for ( int i =0; i < capNumInEachSample.size(); i++ ) {
			for ( int j=i+1; j < capNumInEachSample.size(); j++ ) {
				njnk += capNumInEachSample.get( i)*capNumInEachSample.get(j);
			}
		}
		//System.out.println( "ii1Fi: " + ii1Fi + " njnk: " + njnk );
		
		Double r1 = (1.0*N01*ii1Fi) / (2.0*njnk) - 1.0;
		if ( r1 < 0.0)
			r1 = 0.0;
		Double r2 =  (1.0*N02*ii1Fi) / (2.0*njnk) - 1.0;
		if ( r2 < 0.0)
			r2 = 0.0;
		Double r3 =  (1.0*N03*ii1Fi) / (2.0*njnk) - 1.0;
		if ( r3 < 0.0)
			r3 = 0.0;
		//System.out.println ( "---------------- r " + r1 + " " +  r2   + " " +  r3  );
		
		Integer N1 = this.DoubleFormatInt( N01 + (1.0*numForEachFreq.get(1) *  r1 ) / C1);
		Integer N2 = this.DoubleFormatInt( N02 + (1.0*numForEachFreq.get(1) *  r2 ) / C2);
		System.out.println( C3 + " " + r3 + " " + N03);
		Integer N3 = this.DoubleFormatInt( N03 + (1.0*numForEachFreq.get(1) *  r3 ) / C3);
		
		/*
		System.out.println ( C1 + " " + C2 + " " + C3 );
		System.out.println ( N01+ " " + N02 + " " + N03);
		System.out.println ( Math.sqrt(r1 ) + " " +  Math.sqrt(r2 )  + " " +  Math.sqrt(r3 ) );
		System.out.println( N1 + " " + N2 + " " + N3 );
		*/
		
		Integer[] results = {N1, N2, N3, numDistinctBugs };
		return results;
	}

	public static void main ( String args[] ) {
		MthCRCAlgorithm mth = new MthCRCAlgorithm();
		
		TreeMap<Integer, ArrayList<TestReport>> captureProcess = new TreeMap<Integer, ArrayList<TestReport>>();
		mth.obtainRecaptureResults(captureProcess);
	}
}	
