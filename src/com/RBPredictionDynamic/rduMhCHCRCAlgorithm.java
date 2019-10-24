package com.RBPredictionDynamic;

import java.util.ArrayList;

public class rduMhCHCRCAlgorithm extends rduCRCAlgorithm{

	public rduMhCHCRCAlgorithm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Integer[] obtainPredictedTotalPopulation(ArrayList<ArrayList<Integer>> CRCMatrix) {
		// TODO Auto-generated method stub
		Integer detectedBugs = CRCVariableTool.calculateCRCVariable_D(CRCMatrix);
		Integer captureNumber = CRCVariableTool.calculateCRCVariable_t(CRCMatrix);
		Integer[] bugInKCaptures = CRCVariableTool.calculateCRCVariable_Fk(CRCMatrix);   //Fk
		
		
		Double NValue = 0.0;
		if (captureNumber*bugInKCaptures[1] > 2*bugInKCaptures[2]  && captureNumber * bugInKCaptures[2] > 3*bugInKCaptures[3] 
				&& 3*bugInKCaptures[1]*bugInKCaptures[3] > 2*bugInKCaptures[2]*bugInKCaptures[2] ) {
			NValue = detectedBugs +  ( (1.0*bugInKCaptures[1]*bugInKCaptures[1]) / (2.0*bugInKCaptures[2]) ) * ( 1.0- 2.0*bugInKCaptures[2] /
					(captureNumber*bugInKCaptures[1]) ) / ( 1.0- 3.0*bugInKCaptures[3]/ (captureNumber * bugInKCaptures[2]));
			//System.out.println( "The refined NValue is : " + NValue);
		}else {
			if ( bugInKCaptures[2] == 0 )
				bugInKCaptures[2] = 1;
			NValue = detectedBugs + (1.0*bugInKCaptures[1] * bugInKCaptures[1]) / (2.0* bugInKCaptures[2] );
			//System.out.println( "The default NValue is : "  + NValue );
		}
		int N = CRCVariableTool.DoubleFormatInt( NValue );
		
		Integer[] results = {N, N, N, detectedBugs };
		return results;
	}
}
