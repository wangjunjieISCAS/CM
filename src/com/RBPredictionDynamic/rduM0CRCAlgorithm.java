package com.RBPredictionDynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class rduM0CRCAlgorithm extends rduCRCAlgorithm{
	
	@Override
	public Integer[] obtainPredictedTotalPopulation(ArrayList<ArrayList<Integer>> CRCMatrix) {
		// TODO Auto-generated method stub
		Integer detectedBugs = CRCVariableTool.calculateCRCVariable_D(CRCMatrix);
		/*
		Integer[] predictedBugList = new Integer[CRCMatrix.size()-1];
		for ( int i = 0; i < CRCMatrix.size()-1 ; i++ ) {
			Integer predictedBugNum = this.obtainPredictedTotalPopulationForSep(CRCMatrix, i );
			predictedBugList[i] = predictedBugNum;
		}
		
		List<Integer> bugListRank = Arrays.asList( predictedBugList );
		Collections.sort( bugListRank );
		
		int mid = (bugListRank.size() -1) /2;
		Integer[] results = { bugListRank.get(mid), bugListRank.get(mid), bugListRank.get(mid), detectedBugs };
		*/
		int sepCap = CRCMatrix.size()-2;
		Integer bugNumInCap1 = CRCVariableTool.calculateCRCVariables_M0_BugsInCaptures(CRCMatrix, 0, sepCap );
		Integer bugNumInCap2 = CRCVariableTool.calculateCRCVariables_M0_BugsInCaptures(CRCMatrix, sepCap+1, CRCMatrix.size()-1 );
		Integer bugNumShared = CRCVariableTool.calculateCRCVariables_M0_BugsAmongCaptures(CRCMatrix, 0, sepCap, sepCap+1, CRCMatrix.size()-1 );
		
		Integer predictedBugNum = bugNumInCap1 * bugNumInCap2;
		if ( bugNumShared != 0 )
			predictedBugNum = (int) predictedBugNum / bugNumShared;

		Integer[] results = { predictedBugNum, predictedBugNum, predictedBugNum, detectedBugs };
		return results;
	}
}
