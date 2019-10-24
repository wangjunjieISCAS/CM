package com.RBPredictionDynamic;

import java.util.ArrayList;

public class rduMtCHCRCAlgorithm extends rduCRCAlgorithm {

	public rduMtCHCRCAlgorithm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Integer[] obtainPredictedTotalPopulation(ArrayList<ArrayList<Integer>> CRCMatrix) {
		// TODO Auto-generated method stub
		Integer detectedBugs = CRCVariableTool.calculateCRCVariable_D(CRCMatrix);
		Integer[] bugInKCaptures = CRCVariableTool.calculateCRCVariable_Fk(CRCMatrix);   //Fk
		Integer[] bugOnlyInISample = CRCVariableTool.calculateCRCVariables_Zi( CRCMatrix );    //Zi
		
		int sumZ = 0;
		for ( int i =1; i < bugOnlyInISample.length; i++ ){
			for ( int j=i+1; j < bugOnlyInISample.length; j++){
				sumZ += bugOnlyInISample[i] * bugOnlyInISample[j];
			}
		}
		
		Double NValue = 1.0*detectedBugs + 1.0* sumZ / ( bugInKCaptures[2] + 1 );
		int N = CRCVariableTool.DoubleFormatInt( NValue );
		
		Integer[] results = {N, N, N, detectedBugs };
		return results;
	}
}
