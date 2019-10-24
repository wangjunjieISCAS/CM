package com.RBPredictionDynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.data.TestReport;

public class rduMhJKCRCAlgorithm extends rduCRCAlgorithm{

	public rduMhJKCRCAlgorithm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Integer[] obtainPredictedTotalPopulation(ArrayList<ArrayList<Integer>> CRCMatrix) {
		// TODO Auto-generated method stub
		Integer detectedBugs = CRCVariableTool.calculateCRCVariable_D(CRCMatrix);
		Integer captureNumber = CRCVariableTool.calculateCRCVariable_t(CRCMatrix);
		Integer[] bugInKCaptures = CRCVariableTool.calculateCRCVariable_Fk(CRCMatrix);   //Fk
		
		Double NJ1Value = 1.0*detectedBugs + ( ( captureNumber -1) / captureNumber ) * 1.0* bugInKCaptures[1];
		//System.out.println( "NJ1: " + NJ1Value );
		Integer NJ1 = CRCVariableTool.DoubleFormatInt( NJ1Value );
		
		Double NJ2Value = 1.0*detectedBugs + ((2*captureNumber-3)/captureNumber) * bugInKCaptures[1] - 
				( ((captureNumber-2)*(captureNumber-2)) / (captureNumber*(captureNumber-1)))* 1.0* bugInKCaptures[2];
		//System.out.println( "NJ2: " + NJ2Value );
		Integer NJ2 = CRCVariableTool.DoubleFormatInt( NJ2Value );
		
		Double NJ3Value = 1.0*detectedBugs + ((3*captureNumber-6)/captureNumber)* bugInKCaptures[1] - 
				( (3*captureNumber*captureNumber - 15*captureNumber + 19) / (captureNumber*(captureNumber-1))) * bugInKCaptures[2] + 
				( ((captureNumber-3)*(captureNumber-3)*(captureNumber-3)) / ( captureNumber* (captureNumber-1)*(captureNumber-2))) * 1.0*bugInKCaptures[3];
		//System.out.println( "NJ3: " + NJ3Value );			
		Integer NJ3 = CRCVariableTool.DoubleFormatInt( NJ3Value );
		
		Integer[] results = {NJ1, NJ2, NJ3, detectedBugs.intValue() };
		return results;
	}

}
