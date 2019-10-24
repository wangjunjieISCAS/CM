package com.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import com.data.Constants;
import com.data.TestProject;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;


/*
 * ���в����µ����ܶ�
 */
public class TuneOptimalParameters {

	public TuneOptimalParameters() {
		// TODO Auto-generated constructor stub
	}
	
	//����projectList�е�������Ŀ���õ���Ѳ���
	//%bug���� paraMinBugThres, %reducedCost���� paraMinReducedCostThres, ���������������Ļ����ϣ�ѡ��F1���Ĳ���;
	//�õ�����projectList������Ŀ�е�medianValue
	//����Ѳ��������������Ͳ���ÿ�ζ�������
	public void obtainPerformanceUnderOptimalParameterValues ( String folderName, String outFileName ) {
		PerformanceReader perfReader = new PerformanceReader();
		
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectList( Constants.projectFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
				
		TreeMap<Integer, Double[]> performanceList = new TreeMap<Integer, Double[]>();
		TreeMap<Integer, String> bestParaList = new TreeMap<Integer, String>();
		for ( int i = Constants.EVALUATION_TIME_SERIES_BEGIN; i < projectList.size(); i++ ) {  //Constants.EVALUATION_TIME_SERIES_BEGIN
			//train set is projectList(0) - projectList(beginTaskId-1)
			
			String formerProjectName = projectList.get(i-1).getProjectName();
			String[] temp = formerProjectName.split("-");
			Integer formerProjectId = Integer.parseInt( temp[0]);
			String bestPara = this.computeBestParameterForTaskSubset( folderName, formerProjectId );   //
			
			String curProjectName = projectList.get(i).getProjectName();
			String[] curTemp = curProjectName.split("-");
			Integer curProjectId = Integer.parseInt( curTemp[0]);
			Double[] performance = perfReader.readPerformanceSpecificParameterSpecificProject(folderName, Constants.attrName, bestPara, curProjectId );
			
			performanceList.put( curProjectId, performance );
			bestParaList.put( curProjectId, bestPara );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File( outFileName )));
			for ( Integer projectId : performanceList.keySet() ) {
				//System.out.println( projectId + " " + bestParaList.get( projectId) + " " + performanceList.get( projectId ));
				
				writer.write( projectId + "," );
				Double[] performance = performanceList.get( projectId );
				for ( int i=0; i < performance.length; i++  ) {
					writer.write( performance[i] + ",");
				}
				
				String bestPara = bestParaList.get( projectId );
				String[] temp = bestPara.split( "-");
				writer.write( "," + "," );
				for ( int i = 0; i < temp.length; i++ )
					writer.write( temp[i] + ",");
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String computeBestParameterForTaskSubset ( String folderName, int maxTaskId ) {
		PerformanceReader perfReader = new PerformanceReader();
		HashMap<String, HashMap<Integer, Double[]>> attrValuesList = perfReader.readPerformanceAllParameters(folderName, Constants.attrName, maxTaskId);
		//<para, <projectId, performance>>
		
		HashMap<String, Double[]> medianValuesPara = new HashMap<String, Double[]>();
		for ( String para:  attrValuesList.keySet() ) {
			HashMap<Integer, Double[]> attrValues = attrValuesList.get( para );
			Double[] medianAttrValues = this.obtainMedianPerformance(attrValues);
			
			medianValuesPara.put( para, medianAttrValues );
		}
				
		//parameter, number
		String optPara = this.findMaxF1( medianValuesPara );
		return optPara;				
	}	
	
	public Double[] obtainMedianPerformance ( HashMap<Integer, Double[]> attrValues ) {
		TreeMap<Integer, ArrayList<Double>> newAttrValues = new TreeMap<Integer, ArrayList<Double>>();   //id is the index of the attr
		for ( Integer projectId : attrValues.keySet() ) {
			Double[] values = attrValues.get( projectId );
			for ( int  i=0; i <values.length; i++ ) {
				
				ArrayList<Double> newValues = new ArrayList<Double>();
				if ( newAttrValues.containsKey( i ) ) {
					newValues = newAttrValues.get( i );
				}
				newValues.add( values[i] );
				newAttrValues.put( i, newValues );
			}
		}
		
		Double[] medianAttrValues = new Double[Constants.attrName.length];
		int index = 0;
		for ( Integer attr : newAttrValues.keySet() ) {
			ArrayList<Double> newValues = newAttrValues.get( attr );
			
			Collections.sort( newValues );
			double median = 0.0;
			int midIndex = newValues.size() / 2;
			if ( newValues.size() % 2 == 1 ) {
				median = newValues.get( midIndex );
			}
			else {
				median = ( newValues.get( midIndex -1) + newValues.get(midIndex)) / 2;
			}
			
			medianAttrValues[index++] = median;
		}		
		return medianAttrValues;
	}
	
	
	//M0�õ��� bugs > 0.965, report > 0.2, maximize F1
	//Mth
	public String findMaxF1 ( HashMap<String, Double[]> performance ) {
		double maxF1 = -1.0;
		double maxBug = 0.0;   //�޸����߼������ҵ�����bug%��Ȼ���������bug%�� ���F1
		for ( String para : performance.keySet() ){
			Double[] value = performance.get( para );
			if ( value[0] > maxBug ){
				maxBug = value[0];
			}
		}
				
		String optPara = "";
		for ( String para : performance.keySet() ) {
			Double[] value = performance.get( para );
			if ( value[0] == maxBug && value[2] >= maxF1 ) {
				maxF1 = value[2];
				optPara = para;
			}
		}
		
		return optPara;
	}	
	
	/*cmb��ѵĲ�������dynamic�е�Ч������Ϊ���������֮��ıȽϡ���ͬһ�������±Ƚ�dynamic��comb
	 * */
	public void obtainPerformanceUnderSpeficParameterValues ( String folderName, String outFileName ) {
		PerformanceReader perfReader = new PerformanceReader();
		
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectList( Constants.projectFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
				
		TreeMap<Integer, Double[]> performanceList = new TreeMap<Integer, Double[]>();
		TreeMap<Integer, String> bestParaList = new TreeMap<Integer, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( "data/output/performanceLightPred/final-performance-cmbCRC-underBestPara.csv" )));	
			String line = "";
			while ( (line = br.readLine()) != null ){
				String[] temp = line.split( ",");
				System.out.println( line + " " + temp[0] );
				Integer projectId = Integer.parseInt( temp[0]);
				String bestPara = temp[6] + "-" + temp[7];
				//System.out.println( bestPara );
				bestParaList.put( projectId, bestPara );
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		for ( int i = Constants.EVALUATION_TIME_SERIES_BEGIN; i < projectList.size(); i++ ) {
			String curProjectName = projectList.get(i).getProjectName();
			String[] curTemp = curProjectName.split("-");
			Integer curProjectId = Integer.parseInt( curTemp[0]);
			String bestPara = bestParaList.get( curProjectId );			
			
			Double[] performance = perfReader.readPerformanceSpecificParameterSpecificProject(folderName, Constants.attrName, bestPara, curProjectId );
			
			performanceList.put( curProjectId, performance );
			bestParaList.put( curProjectId, bestPara );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( new File( outFileName )));
			for ( Integer projectId : performanceList.keySet() ) {
				writer.write( projectId + "," );
				Double[] performance = performanceList.get( projectId );
				for ( int i=0; i < performance.length; i++  ) {
					writer.write( performance[i] + ",");
				}
				
				String bestPara = bestParaList.get( projectId );
				String[] temp = bestPara.split( "-");
				writer.write( "," + "," );
				for ( int i = 0; i < temp.length; i++ )
					writer.write( temp[i] + ",");
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main ( String[] args ) {
		TuneOptimalParameters tune = new TuneOptimalParameters();
		
		String[] CRCTypeList = { "M0", "Mth", "MhJK", "MhCH", "MtCH"};
		for ( int i =0; i < CRCTypeList.length; i++ ){
			String CRCType = CRCTypeList[i];
			String folderName = "data/output/performanceLightPred/CRC" + CRCType;
			tune.obtainPerformanceUnderOptimalParameterValues(folderName, "data/output/performanceLightPred/performance-cmb" + CRCType + "CRC-underBestPara.csv");	
		}
		
		
		
		//String folderName = "data/output/performanceLightPred/CRCdyn";
		//tune.obtainPerformanceUnderSpeficParameterValues(folderName, "data/output/performanceLightPred/performance-dynCRC-underSpecificPara.csv");
	}
}
