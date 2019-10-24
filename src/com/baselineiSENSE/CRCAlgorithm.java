package com.baselineiSENSE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import com.data.TestReport;

public class CRCAlgorithm {
	public Integer[] obtainRecaptureResults ( TreeMap<Integer, ArrayList<TestReport>> captureProcess ) {  
		return null;
	}
	
	public int DoubleFormatInt(Double dou){
		DecimalFormat df = new DecimalFormat("######0"); //四色五入转换成整数
		int result =  Integer.parseInt(df.format(dou));
		
		return result; 
	}
}
