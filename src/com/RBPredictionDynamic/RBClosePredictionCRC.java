package com.RBPredictionDynamic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.evaluation.PerformanceEvaluation;


public class RBClosePredictionCRC extends RBBasePrediction {
	rduCRCAlgorithm CRCAlgorithm;
	
	HashMap<Integer, Integer> sampleSizeList ;
	HashMap<Integer, Double> simThresList;
	
	public RBClosePredictionCRC ( ){
		CRCAlgorithm = new rduM0CRCAlgorithm();
	}
	
	public RBClosePredictionCRC ( String CRCType ){
		if ( CRCType.equals( "M0")){
			CRCAlgorithm = new rduM0CRCAlgorithm();
		}
		if ( CRCType.equals( "Mth")){
			CRCAlgorithm = new rduMthCRCAlgorithm();
		}
		if ( CRCType.equals( "MhCH")){
			CRCAlgorithm = new rduMhCHCRCAlgorithm();
		}
		if ( CRCType.equals( "MtCH")){
			CRCAlgorithm = new rduMtCHCRCAlgorithm();
		}
		if ( CRCType.equals( "MhJK")){
			CRCAlgorithm = new rduMhJKCRCAlgorithm();
		}
		else {
			CRCAlgorithm = new rduM0CRCAlgorithm();
		}
	}
	
	public RBClosePredictionCRC ( String CRCType, String parameterFile ){
		if ( CRCType.equals( "M0")){
			CRCAlgorithm = new rduM0CRCAlgorithm();
		}
		if ( CRCType.equals( "Mth")){
			CRCAlgorithm = new rduMthCRCAlgorithm();
		}
		if ( CRCType.equals( "MhCH")){
			CRCAlgorithm = new rduMhCHCRCAlgorithm();
		}
		if ( CRCType.equals( "MtCH")){
			CRCAlgorithm = new rduMtCHCRCAlgorithm();
		}
		if ( CRCType.equals( "MhJK")){
			CRCAlgorithm = new rduMhJKCRCAlgorithm();
		}
		else {
			CRCAlgorithm = new rduM0CRCAlgorithm();
		}
		
		sampleSizeList = new HashMap<Integer, Integer>();
		simThresList = new HashMap<Integer, Double>();
		try {
			BufferedReader reader = new BufferedReader ( new FileReader ( new File ( parameterFile )));
			String line = "";
			while ( (line = reader.readLine() ) != null ){
				String[] temp = line.split( ",");
				Integer index = Integer.parseInt( temp[0] );
				Integer sampleSize = Integer.parseInt( temp[1] );
				Double simThres = Double.parseDouble( temp[2] );
				
				sampleSizeList.put( index, sampleSize );
				simThresList.put( index, simThres );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Double[] conductClosePrediction ( TestProject project, String[] thresList) {
		PerformanceEvaluation evaluation = new PerformanceEvaluation( "data/input/bugCurveStatistics.csv");
		
		Integer equalTimesThres = 2;
		/*
		Integer sampleSize = Integer.parseInt(thresList[0]) ;
		Double simThres = Double.parseDouble( thresList[1] );
		*/
		String[] temp = project.getProjectName().split("-");
		Integer index = Integer.parseInt( temp[0] );
		//System.out.println ( project.getProjectName() + " " + index );
		Integer sampleSize = sampleSizeList.get( index );
		Double simThres = simThresList.get( index );
		
		ArrayList<Integer[]> CRCResultHistory = new ArrayList<Integer[]>();
		
		ArrayList<Integer> bugTagList = new ArrayList<Integer>();
		ArrayList<Integer> reportPredDupTag = new ArrayList<Integer>();
		//���ڵ�0�����棬dupTagΪ0����1�����棬����͵�0���������ԣ�������ƣ�dupTagΪ0������dupTagΪ1����i�����棬�����ǰ�����dupTag���б���������ԣ�������ƣ���Ϊ����ı�ţ������¿�һ����
		
		ArrayList<ArrayList<Integer>> CRCMatrix = new ArrayList<ArrayList<Integer>>();
		ArrayList<HashMap<String, Integer>> histReportTermsList = new ArrayList<HashMap<String, Integer>>();
		
		ArrayList<TestReport> curCaptureReports = new ArrayList<TestReport>();
		
		HashSet<String> noDupTag = new HashSet<String>();
		for ( int i =0; i< project.getTestReportsInProj().size(); i++ ) {
			String bugTag = project.getTestReportsInProj().get( i ).getBugTag();
			if ( bugTag.equals("���ͨ��")) {
				noDupTag.add( project.getTestReportsInProj().get(i).getDupTag() );
			}
		}
		int groundTruthTotalBugs = noDupTag.size();
		
		Integer curGroupNum = 0;
		int i = 0;
		for ( ; i < project.getTestReportsInProj().size(); i++ ) {
			curCaptureReports.add( project.getTestReportsInProj().get( i ) ) ;
			if ( (i == 0 || (i+1) % sampleSize !=0) && i != project.getTestReportsInProj().size()-1  )
				continue;
			
			int sampleIndex = i / sampleSize;
			//��һ��capture�����д���
			int priorRowSize = 0;
			if ( CRCMatrix.size() > 0 ){
				priorRowSize = CRCMatrix.get( CRCMatrix.size()-1 ).size();
			}
			ArrayList<Integer> curCaptureRow = new ArrayList<Integer>();
			for ( int k =0; k < priorRowSize; k++ ){
				curCaptureRow.add( 0 );
			}
			
			for ( int j =0; j < curCaptureReports.size(); j++ ){
				TestReport report = curCaptureReports.get( j );
				HashMap<String, Integer> curReportTerms = WordSegment.obtainUniqueTermForReport( report );
				
				if ( curReportTerms.size() <= 1 ){     //�ù�����Ϊ�Ƿ�Ϊbug���ж�
					bugTagList.add( 0);
					//reportPredDupTag.add( -2 );
		
					continue;
				}
				bugTagList.add( 1 );
				
				Integer dupTag = this.obtainDupTagInfo(curReportTerms, histReportTermsList, reportPredDupTag, simThres);
				if ( dupTag == -1 ) {
					dupTag = ++ curGroupNum;
				}
				reportPredDupTag.add( dupTag );
				//System.out.println ( curReportTerms.toString() );
				System.out.println ( "dupTag: " + dupTag );
				
				//���ĳreport�Ǻ����capture�����report�ظ�����Ĭ����һ��report����Ӧ��Ԫ��������1
				//����CRCMatrix�����capture������
				if ( dupTag < curCaptureRow.size() ){
					int count = curCaptureRow.get( dupTag );
					curCaptureRow.set(dupTag, count+1 );
				}else{
					curCaptureRow.add( 1 );
				}
				histReportTermsList.add( curReportTerms );
				
				if ( histReportTermsList.size() !=  reportPredDupTag.size() ){
					System.out.println( "Wrong!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + histReportTermsList.size() + " " + reportPredDupTag.size() );
				}
			}
			CRCMatrix.add( curCaptureRow );
			curCaptureReports.clear();          //һ��capture������֮�󣬽�curCaptureReports���
			
			if( sampleIndex >= 4 ){
				//�õ�CRC���
				Integer[] result = CRCAlgorithm.obtainPredictedTotalPopulation( CRCMatrix  );
				Integer[] extendResult = { result[0], result[1], result[2], result[3], (i+1), groundTruthTotalBugs };
				CRCResultHistory.add( extendResult );
				
				System.out.println( "CRC results : " + Arrays.asList( result ) );
				//�����õ�������������whetherCanTerminate����Ҫ��predicted number = �Ѿ����ֵ�bug number
				Boolean isClose = this.whetherCanTerminate( CRCResultHistory, equalTimesThres );
				if ( isClose == true ) {
					break;
				}
			}
		}	
		//iΪ���յ���report��Ŀ
		Double[] minPerformance = evaluation.evaluatePerformanceByEndReport ( i, project );
		Double[] performance = new Double[minPerformance.length+1];
		for( int k =0; k < minPerformance.length; k++ ) {
			performance[k] = minPerformance[k];
		}
		performance[performance.length-1] = 1.0*reportPredDupTag.size();
				
		System.out.print ( "************************************ ");
		for ( int k =0; k < performance.length; k++ )
			System.out.print( performance[k] + " " );
		System.out.println( );
		/*
		WriterTool writerTool = new WriterTool();
		String detailOutputFile = "data/output/predictionDetails/detail-" + project.getProjectName();
		writerTool.outputPredictionDetails(CRCResultHistory, detailOutputFile );
		String tagOutputFile = "data/output/projectsWithPredictedDupTag/dupTag-" + project.getProjectName();
		writerTool.outputPredictionDupTag(project, bugTagList, reportPredDupTag, tagOutputFile );
		*/
		return performance;
	}
	
	public Boolean whetherCanTerminate ( ArrayList<Integer[]> captureResults, Integer equalTimesThres ) {   //NJ1, NJ2, NJ3, true totalNumBugs
		if ( captureResults.size() < equalTimesThres )
			return false;
		
		int count = 0;
		for ( int i = captureResults.size()-1; i > 0 ; i-- ) {
			Integer[] curResults = captureResults.get( i);
			Integer[] priorResults = captureResults.get( i-1 );
			
			if ( (curResults[2] == priorResults[2]) && curResults[2] != 0  )
				count ++;
			else
				count = 0;
			
			if ( count >= equalTimesThres )
				return true;
		}
		return false;
	}
	
	
	public static void main ( String[] args ) {
		//���û��������
		RBClosePredictionCRC closePrediction = new RBClosePredictionCRC( "MhCH", "data/input/bestPara-MhCH.csv" );
		String[] thresList = { " ", " "};
		
		closePrediction.predictCloseTimeForProjects( Constants.projectFolder, Constants.taskFolder, 
				"data/output/performanceLightPred/CRCdyn/performance-dynCRC-" + thresList[0] + "-" + 
				 thresList[1] +  ".csv", thresList );
		
		/*
		��Ҫ������.��Ҫ�޸�ԭ������
		Integer[] sampleSizeList = { 6, 8, 10, 12, 14, 16, 18, 20 };
		Double[] simThresList = { 0.66, 0.68, 0.70, 0.72, 0.74, 0.76, 0.78, 0.80, 0.82, 0.84, 0.85, 0.88 };
		for ( int i =0; i < sampleSizeList.length; i++ ){
			for ( int j =0; j < simThresList.length; j++ ){
				String[] thresList = { sampleSizeList[i].toString(), simThresList[j].toString() };
				
				closePrediction.predictCloseTimeForProjects( Constants.projectFolder, Constants.taskFolder, 
						"data/output/performanceLightPred/CRCdyn/performance-dynCRC-" + thresList[0] + "-" + 
						 thresList[1] +  ".csv", thresList);
			}
		}
		*/
	}
}
