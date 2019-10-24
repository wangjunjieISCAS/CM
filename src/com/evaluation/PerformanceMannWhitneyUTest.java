package com.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import com.data.Constants;

/*
 * 可以得到p-value和effect size，需要分别运行
 */
public class PerformanceMannWhitneyUTest {
	String[] categoryRank = { "CRC", "iSENSE2.0", "contribution-CRC-cat1", "contribution-iSENSE2.0-cat1",  
			"contribution-CRC-cat2" ,  "contribution-iSENSE2.0-cat2", "contribution-CRC-cat3", "contribution-iSENSE2.0-cat3"};
	//String[] categoryRank =  { "iSENSE2.0", "iSENSE", "Rayleigh"}; 
	public PerformanceMannWhitneyUTest() {
		// TODO Auto-generated constructor stub
	}
	
	public void conductMannWhitneyTest ( String folderName, String outputFile  ) {
		
		PerformanceReader perfReader = new PerformanceReader();
		HashMap<String, HashMap<Integer, Double[]>> totalPerformance = new HashMap<String, HashMap<Integer, Double[]>>();
		for ( int i = 0; i < categoryRank.length; i++ ){
			String methodName = categoryRank[i];
			String fileName = folderName + "/" + methodName + ".csv";
			System.out.println( fileName );
			HashMap<Integer, Double[]> performance = perfReader.readPerformanceNoHeaderOnlyPID( fileName);
			
			totalPerformance.put( methodName, performance );
		}
		
		MannWhitneyUTest test = new MannWhitneyUTest();
		
		double[][] bugTest = new double[categoryRank.length][categoryRank.length];
		double[][] reportTest = new double[categoryRank.length][categoryRank.length];
		double[][] F1Test = new double[categoryRank.length][categoryRank.length];
		for ( int i =0; i < bugTest.length; i++ ) {
			for ( int j =0; j < bugTest[0].length; j++) {
				bugTest[i][j] = -1.0;
				reportTest[i][j] = -1.0;
				F1Test[i][j] = -1.0;
			}
		}
		
		for ( int j =0; j < categoryRank.length; j++ ) {	
			for ( int k =j+1; k < categoryRank.length; k++ ) {   //test between j and k 
				for ( int i =0; i < Constants.attrName.length; i++ ) {
					HashMap<Integer, Double[]> jPerformance = totalPerformance.get( categoryRank[j]);
					double[] jValues = new double[jPerformance.size()];
					int index =0;
					for ( Integer projectId : jPerformance.keySet() ){
						jValues[index++] = jPerformance.get( projectId)[i];
					}
					
					HashMap<Integer, Double[]> kPerformance= totalPerformance.get( categoryRank[k] );
					double[] kValues = new double[kPerformance.size()];
					index = 0;
					for ( Integer projectId : kPerformance.keySet() ){
						kValues[index++] = kPerformance.get( projectId)[i];
					}
					
					//double pValue = test.mannWhitneyUTest(jValues, kValues);
					//double pValue = this.computeCliffDelta(jValues, kValues);
					double uValue = test.mannWhitneyU( jValues, kValues );
					double pValue = (2.0*uValue) / (jValues.length * kValues.length ) - 1;   //this is Cliff's delta
					
					if ( i == 0 )
						bugTest[j][k] = pValue;
					else if ( i == 1)
						reportTest[j][k] = pValue;
					else
						F1Test[j][k] = pValue;
				}
			}
		}
		
		this.generateTestFile( outputFile + "EffTest-bug.csv", bugTest );
		this.generateTestFile(  outputFile + "EffTest-report.csv", reportTest );
		this.generateTestFile(  outputFile + "EffTest-F1.csv", F1Test);
	}
	
	
	public void generateTestFile ( String outFileName, double[][] testResult ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( outFileName )));
			
			writer.write ( "" + ",");
			for ( int i =0; i < categoryRank.length; i++ ) {
				writer.write( i+1 + ",");
			}
			writer.newLine();
			
			for ( int j =0; j < categoryRank.length; j++ ) {	
				writer.write( j+1 + ",");
				for ( int k =0; k < categoryRank.length; k++ ) {  
					double temp = testResult[j][k];
					if ( j ==0 && k ==0 )
						writer.write( "0" + ",");
					else if ( temp < 0.0 )
						writer.write( " " + ",");
					else if ( temp < 0.0009 )
						writer.write( "0.000" + ",");
					else
						writer.write( temp + ",");
				}
				writer.newLine();
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main ( String args[] ) {
		PerformanceMannWhitneyUTest test = new PerformanceMannWhitneyUTest();
		//test.conductMannWhitneyTest( "data/output/baseline" , "data/output/baseline/" );
		test.conductMannWhitneyTest( "data/output/baseline-dyn", "data/output/baseline-dyn/" );
	}
}
