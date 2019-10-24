package com.RBPredictionDynamic;

import java.util.ArrayList;
import java.util.Arrays;

public class rduMthCRCAlgorithm extends rduCRCAlgorithm{
	/*
	 * 简化版的CRC algorithm，外部接口提供获取各种变量的方式；这里只需要基于变量的值，得到predicted total bug number
	 */
	
	public rduMthCRCAlgorithm() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public Integer[] obtainPredictedTotalPopulation(ArrayList<ArrayList<Integer>> CRCMatrix) {
		// TODO Auto-generated method stub
		Integer detectedBugs = CRCVariableTool.calculateCRCVariable_D(CRCMatrix);
		Integer captureNumber = CRCVariableTool.calculateCRCVariable_t(CRCMatrix);
		Integer[] bugsInEachCapture = CRCVariableTool.calculateCRCVariable_Nj(CRCMatrix);   //Nj, not unique bugs
		Integer[] bugInKCaptures = CRCVariableTool.calculateCRCVariable_Fk(CRCMatrix);   //Fk
		
		//System.out.println ( detectedBugs + " " + captureNumber );
		//System.out.println( Arrays.asList(bugsInEachCapture) .toString() );
		//System.out.println ( Arrays.asList( bugInKCaptures).toString() );
		
		Integer iFiSum = 0;
		for ( int i =0; i < bugInKCaptures.length; i++ ) {
			iFiSum +=  i * bugInKCaptures[i];
		}
		Integer totalBugsAllCaptures = 0;
		for ( int i =0; i < bugsInEachCapture.length; i++ ){
			totalBugsAllCaptures += bugsInEachCapture[i];
		}
		/*
		if (  numForEachFreq.size() < 2 ) {
			Integer[] results = {0, 0, 0, numDistinctBugs };
			return results;
		}
		*/
		
		if ( !iFiSum.equals( totalBugsAllCaptures ) ) {
			System.out.println( "confidence checking!! " + iFiSum + " " + totalBugsAllCaptures );
		}
		
		Double C1 = 1.0 - ( 1.0*bugInKCaptures[1]) / (1.0* iFiSum);
		Double C2 = 1.0 - (1.0* bugInKCaptures[1] - 2.0*bugInKCaptures[2]/(captureNumber-1)) / (1.0*iFiSum );
		Double C3 = 1.0 - (1.0*bugInKCaptures[1] - 2.0*bugInKCaptures[2]/(captureNumber-1) + 
				6.0*bugInKCaptures[3] / ((captureNumber-1)*(captureNumber-2)) ) / (1.0*iFiSum );
		if ( C1 == 0 || C1.equals( Double.NaN))
			C1 = 0.1;
		if ( C2 == 0 || C2.equals( Double.NaN))
			C2 = 0.1;
		if ( C3 == 0 || C3.equals( Double.NaN))
			C3 = 0.1;
		//System.out.println ( "---------------- C " +  C1 + " " + C2 + " " + C3 );
		
		Integer N01 = (int) ( detectedBugs / C1);
		Integer N02 = (int) (detectedBugs / C2);
		Integer N03 = (int) (detectedBugs / C3);
		//System.out.println ( "---------------- N " + N01+ " " + N02 + " " + N03);
		
		Integer ii1Fi = 0;
		for ( int i =1; i < bugInKCaptures.length; i++ ) {
			ii1Fi += i * (i-1) * bugInKCaptures[i];
		}
		Integer njnk = 0;
		for ( int i =0; i < bugsInEachCapture.length; i++ ) {
			for ( int j = i+1; j < bugsInEachCapture.length; j++) {
				njnk += bugsInEachCapture[i]*bugsInEachCapture[j];
			}
		}
		Double r1 = (1.0*N01*ii1Fi) / (2.0*njnk) - 1.0;
		if ( r1 < 0.0)
			r1 = 0.0;
		Double r2 =  (1.0*N02*ii1Fi) / (2.0*njnk) - 1.0;
		if ( r2 < 0.0)
			r2 = 0.0;
		Double r3 =  (1.0*N03*ii1Fi) / (2.0*njnk) - 1.0;
		if ( r3 < 0.0)
			r3 = 0.0;
		System.out.println ( "---------------- r " + r1 + " " +  r2   + " " +  r3  );
		
		Integer N1 = CRCVariableTool.DoubleFormatInt( N01 + (1.0*bugInKCaptures[1] *  r1 ) / C1);
		Integer N2 = CRCVariableTool.DoubleFormatInt( N02 + (1.0*bugInKCaptures[1] *  r2 ) / C2);
		System.out.println( C3 + " " + r3 + " " + N03);
		Integer N3 = CRCVariableTool.DoubleFormatInt( N03 + (1.0*bugInKCaptures[1] *  r3 ) / C3);
		
		Integer[] results = {N1, N2, N3, detectedBugs };
		return results;
	}
}
