package com.RBPredictionDynamic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RBCRCVariableValue {

	public RBCRCVariableValue() {
		// TODO Auto-generated constructor stub
	}
	
	public Integer calculateCRCVariable_D ( ArrayList<ArrayList<Integer>> CRCMatrix ) {
		ArrayList<Integer> CRCMatrixRow = CRCMatrix.get( CRCMatrix.size()-1  );
		int curBugNum = CRCMatrixRow.size();
		
		return curBugNum;
	}
	
	public Integer calculateCRCVariable_t ( ArrayList<ArrayList<Integer>> CRCMatrix) {
		Integer captureNum = CRCMatrix.size();
	
		return captureNum;
	}
	
	public Integer[] calculateCRCVariable_Nj ( ArrayList<ArrayList<Integer>> CRCMatrix ) {   //每次capture结束后才会调用CRC方法进行total number的预测
		Integer captureNum = this.calculateCRCVariable_t(CRCMatrix);	
	
		Integer[] bugsInEachCapture = new Integer[captureNum+1];   //第0维没有数值
		for ( int i =0; i < bugsInEachCapture.length; i++ ) {
			bugsInEachCapture[i] = 0;
		}
		
		for ( int i =0; i < CRCMatrix.size(); i++ ){
			ArrayList<Integer> CRCMatrixRow = CRCMatrix.get( i );
			int bugsThisCapture = 0;
			for ( int j =0; j < CRCMatrixRow.size(); j++ ){
				bugsThisCapture += CRCMatrixRow.get( j );
			}
			bugsInEachCapture[i+1] = bugsThisCapture;
		}
		
		return bugsInEachCapture;
	}
	
	public Integer[] calculateCRCVariable_Fk ( ArrayList<ArrayList<Integer>> CRCMatrix ) {   //number of bugs captured exactly k times in all captures
		Integer rowNum = CRCMatrix.size();
		Integer columnNum = CRCMatrix.get( CRCMatrix.size()-1 ).size();
		Integer[] bugsInKCaptures = new Integer[rowNum*columnNum];   //第0维没有数值, 不能直接用captureNum作为维度，因为会有出现两次的情况
		for ( int i =0; i < bugsInKCaptures.length; i++ ) {
			bugsInKCaptures[i] = 0;
		}
		
		ArrayList<Integer> lastRow = CRCMatrix.get( CRCMatrix.size()-1 );
		Integer[] onesInColumn = new Integer[lastRow.size()];
		for ( int i =0; i < lastRow.size(); i++ ){  //column i
			int onesThisColumn = 0;
			for ( int j =0; j < CRCMatrix.size(); j++ ){
				ArrayList<Integer> curRow = CRCMatrix.get( j );
				if ( curRow.size() > i ){
					onesThisColumn += curRow.get( i );
				}				
			}
			onesInColumn[i] = onesThisColumn;
		}
		
		for ( int k =1; k < bugsInKCaptures.length; k++ ){
			int numberOnesInColumn = 0;
			for ( int j =0; j < onesInColumn.length; j++ ){
				if ( onesInColumn[j] == k )
					numberOnesInColumn ++;
			}
			
			bugsInKCaptures[k] = numberOnesInColumn;
		}
		
		return bugsInKCaptures;
	}
	
	public Integer[] calculateCRCVariables_Zi ( ArrayList<ArrayList<Integer>> CRCMatrix ) {   //number of bugs detected only in the ith capture
		Integer captureNum = this.calculateCRCVariable_t( CRCMatrix );
		Integer[] bugsInICapture = new Integer[captureNum+1];   //第0维没有数值
		for ( int i =0; i < bugsInICapture.length; i++ ) {
			bugsInICapture[i] = 0;
		}
		
		for ( int i =0; i < CRCMatrix.size(); i++ ){
			ArrayList<Integer> CRCRow = CRCMatrix.get( i );
			int bugsInOneCapture = 0;
			for ( int j =0; j < CRCRow.size(); j++ ){
				int value = CRCRow.get( j );
				if ( value == 0 )
					continue;
				
				boolean zeroFlag = true;
				for ( int k =0; k < CRCMatrix.size() && zeroFlag; k++ ){  //是否在其他capture中都是0
					int value2 = 0;
					if ( CRCMatrix.get(k).size() > j ){   //前面的有些行可能没有补齐，都默认为0 
						value2 = CRCMatrix.get(k).get( j );
					}					
					if (value2 == 1 ){
						zeroFlag = false;
					}
				}
				
				if ( zeroFlag ){
					bugsInOneCapture ++;
				}
			}
			
			bugsInICapture[i+1] = bugsInOneCapture;
		}
		
		return bugsInICapture;
	}
	
	//从beginCap到endCap中（包含begin和end），total bug number
	public Integer calculateCRCVariables_M0_BugsInCaptures ( ArrayList<ArrayList<Integer>> CRCMatrix , int beginCap, int endCap) {
		HashMap<Integer, Integer> noDupBugs = new HashMap<Integer, Integer>();
		ArrayList<Integer> CRCLastRow = CRCMatrix.get( CRCMatrix.size()-1 );
		for ( int i =0; i < CRCLastRow.size(); i++ ) {
			noDupBugs.put( i, 0);
		}
		
		for ( int i = beginCap; i <= endCap; i++ ) {
			ArrayList<Integer> CRCRow = CRCMatrix.get( i );
			for ( int j =0 ; j < CRCRow.size(); j++ ) {
				if ( CRCRow.get( j) != 0 ) {
					noDupBugs.put( j, 1);
				}
			}
		}
		
		int bugNum = 0;
		for ( Integer key: noDupBugs.keySet() ) {
			if ( noDupBugs.get( key )!= 0 )
				bugNum++;
		}
		return bugNum;
	}
	
	public Integer calculateCRCVariables_M0_BugsAmongCaptures (ArrayList<ArrayList<Integer>> CRCMatrix , int beginCap1, int endCap1, int beginCap2, int endCap2 ) {
		HashMap<Integer, Integer> noDupBugs1 = new HashMap<Integer, Integer>();
		ArrayList<Integer> CRCLastRow = CRCMatrix.get( CRCMatrix.size()-1 );
		for ( int i =0; i < CRCLastRow.size(); i++ ) {
			noDupBugs1.put( i, 0);
		}
		for ( int i = beginCap1; i <= endCap1; i++ ) {
			ArrayList<Integer> CRCRow = CRCMatrix.get( i );
			for ( int j =0 ; j < CRCRow.size(); j++ ) {
				if ( CRCRow.get( j) != 0 ) {
					noDupBugs1.put( j, 1);
				}
			}
		}
		
		HashMap<Integer, Integer> noDupBugs2 = new HashMap<Integer, Integer>();
		for ( int i =0; i < CRCLastRow.size(); i++ ) {
			noDupBugs2.put( i, 0);
		}
		for ( int i = beginCap2; i <= endCap2; i++ ) {
			ArrayList<Integer> CRCRow = CRCMatrix.get( i );
			for ( int j =0 ; j < CRCRow.size(); j++ ) {
				if ( CRCRow.get( j) != 0 ) {
					noDupBugs2.put( j, 1);
				}
			}
		}
		
		int dupNumAmongCaps = 0;
		for ( Integer key : noDupBugs1.keySet() ) {
			if ( noDupBugs1.get( key) != 0 && noDupBugs2.get(key ) != 0 ) {
				dupNumAmongCaps++;
			}
		}
		return dupNumAmongCaps;
	}
	
	public int DoubleFormatInt(Double dou){
		DecimalFormat df = new DecimalFormat("######0"); //四色五入转换成整数
		int result =  Integer.parseInt(df.format(dou));
		
		return result; 
	}
}
