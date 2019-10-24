package com.RBPredictionCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.RBPredictionCoverage.CoverageMeasurement;
import com.RBPredictionDynamic.RBBasePrediction;
import com.RBPredictionDynamic.RBClosePredictionCRC;
import com.RBPredictionDynamic.WriterTool;
import com.RBPredictionDynamic.rduCRCAlgorithm;
import com.RBPredictionDynamic.rduM0CRCAlgorithm;
import com.RBPredictionDynamic.rduMhCHCRCAlgorithm;
import com.RBPredictionDynamic.rduMhJKCRCAlgorithm;
import com.RBPredictionDynamic.rduMtCHCRCAlgorithm;
import com.RBPredictionDynamic.rduMthCRCAlgorithm;
import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.evaluation.PerformanceEvaluation;

/*
 * CRC+sanity check
 */
public class RBClosePredictionCombCRC extends RBBasePrediction {
	rduCRCAlgorithm CRCAlgorithm;
	CoverageMeasurement coverageCheck;
	
	public RBClosePredictionCombCRC() {
		// TODO Auto-generated constructor stub
		CRCAlgorithm = new rduM0CRCAlgorithm();
		coverageCheck = new CoverageMeasurement ();
	}
	
	public RBClosePredictionCombCRC( String CRCType ){
		coverageCheck = new CoverageMeasurement ();
		
		if ( CRCType.equals( "M0")){
			CRCAlgorithm = new rduM0CRCAlgorithm();
		}
		if ( CRCType.equals( "Mth")){
			CRCAlgorithm = new rduMthCRCAlgorithm();
		}
		if ( CRCType.equals("MtCH")){
			CRCAlgorithm = new rduMtCHCRCAlgorithm();
		}
		if ( CRCType.equals( "MhCH")){
			CRCAlgorithm = new rduMhCHCRCAlgorithm();
		}
		if ( CRCType.equals( "MhJK")){
			CRCAlgorithm = new rduMhJKCRCAlgorithm();
		}
	}
	
	public Double[] conductClosePrediction ( TestProject project, String[] thresList ) {
		PerformanceEvaluation evaluation = new PerformanceEvaluation( "data/input/bugCurveStatistics.csv");
		RBClosePredictionCRC basePrediction = new RBClosePredictionCRC();
		
		Integer sampleSize = Integer.parseInt(thresList[0]) ;
		Integer equalTimesThres = 2;
		Double simThres = Double.parseDouble( thresList[1] );
		Double coverageThres = Double.parseDouble( thresList[2] );
		
		ArrayList<String> taskTerms = project.getTestTask().getTaskDescription();
		
		ArrayList<Integer[]> CRCResultHistory = new ArrayList<Integer[]>();
		
		ArrayList<Integer> bugTagList = new ArrayList<Integer>();
		ArrayList<Integer> reportPredDupTag = new ArrayList<Integer>();
		//���ڵ�0�����棬dupTagΪ0����1�����棬����͵�0���������ԣ�������ƣ�dupTagΪ0������dupTagΪ1����i�����棬�����ǰ�����dupTag���б���������ԣ�������ƣ���Ϊ����ı�ţ������¿�һ����
		
		ArrayList<ArrayList<Integer>> CRCMatrix = new ArrayList<ArrayList<Integer>>();
		ArrayList<HashMap<String, Integer>> histReportTermsList = new ArrayList<HashMap<String, Integer>>();
		ArrayList<HashMap<String, Integer>> curCaptureReportTermsList = new ArrayList<HashMap<String, Integer>>();
		
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
			TestReport report = project.getTestReportsInProj().get( i );
			HashMap<String, Integer> reportTerms = WordSegment.obtainUniqueTermForReport( report );
			curCaptureReportTermsList.add( reportTerms );
			
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
			
			for ( int j =0; j < curCaptureReportTermsList.size(); j++ ){
				HashMap<String, Integer> curReportTerms = curCaptureReportTermsList.get( j );
				
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
				//System.out.println ( "dupTag: " + dupTag );
				
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
			curCaptureReportTermsList.clear();          //һ��capture������֮�󣬽�curCaptureReports���
			
			//coverage-based sanity check 
			Double coverageRatio = coverageCheck.measureCurrentCoverage(taskTerms, histReportTermsList );
			System.out.println ( "coverage check " + coverageRatio + " " + i );
			if ( coverageRatio < coverageThres )
				continue;
			
			if( sampleIndex >= 4 ){
				//�õ�CRC���
				Integer[] result = CRCAlgorithm.obtainPredictedTotalPopulation( CRCMatrix  );
				Integer[] extendResult = { result[0], result[1], result[2], result[3], (i+1), groundTruthTotalBugs };
				CRCResultHistory.add( extendResult );
				
				//System.out.println( "CRC results : " + Arrays.asList( result ) );
				//�����õ�������������whetherCanTerminate����Ҫ��predicted number = �Ѿ����ֵ�bug number
				Boolean isClose = basePrediction.whetherCanTerminate( CRCResultHistory, equalTimesThres );
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
				
		for ( int k =0; k < performance.length; k++ )
			System.out.print( performance[k] + " " );
		System.out.println( );
		
		WriterTool writerTool = new WriterTool();
		String detailOutputFile = "data/output/predictionDetails/detail-" + project.getProjectName();
		writerTool.outputPredictionDetails(CRCResultHistory, detailOutputFile );
		String tagOutputFile = "data/output/projectsWithPredictedDupTag/dupTag-" + project.getProjectName();
		writerTool.outputPredictionDupTag(project, bugTagList, reportPredDupTag, tagOutputFile );
		
		return performance;
	}
	
	public static void main ( String[] args ) {
		
		String CRCType = "MtCH";		
		RBClosePredictionCombCRC closePrediction = new RBClosePredictionCombCRC( CRCType );
		
		Integer[] sampleSizeList = { 6, 8, 10, 12, 14, 16 };  
		Double[] simThresList = { 0.66, 0.70, 0.74, 0.78, 0.82, 0.86 };
		Double[] coverageThresList = { 0.50,  0.60,  0.70,  0.80};
		for ( int i =0; i < sampleSizeList.length; i++ ){
			for ( int j =0; j < simThresList.length; j++ ){
				for ( int k = 0; k < coverageThresList.length; k++ ){
					String[] thresList = { sampleSizeList[i].toString(), simThresList[j].toString(), coverageThresList[k].toString() };
					
					closePrediction.predictCloseTimeForProjects( Constants.projectFolder, Constants.taskFolder, 
							"data/output/performanceLightPred/CRC" + CRCType + "/performance-combCRC" + CRCType + "-" + thresList[0] + "-" + 
							 thresList[1] + "-" + thresList[2] +  ".csv", thresList);
				}
			}
		}	
		
		/*
		Integer[] sampleSizeList = { 6, 8, 10, 12, 14, 16, 18, 20 };  
		Double[] simThresList = { 0.66, 0.70, 0.74, 0.78, 0.82, 0.86 };
		Double[] coverageThresList = { 0.40, 0.50, 0.60, 0.70, 0.80, 0.90};
		*/
		/*
		Integer[] sampleSizeList = { 6, 8, 10, 12, 14, 16, 18, 20 };  
		Double[] simThresList = { 0.66, 0.68, 0.70, 0.72, 0.74, 0.76, 0.78, 0.80, 0.82, 0.84, 0.86, 0.88 };
		Double[] coverageThresList = { 0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90};
		for ( int i =0; i < sampleSizeList.length; i++ ){
			for ( int j =0; j < simThresList.length; j++ ){
				for ( int k = 0; k < coverageThresList.length; k++ ){
					String[] thresList = { sampleSizeList[i].toString(), simThresList[j].toString(), coverageThresList[k].toString() };
					
					closePrediction.predictCloseTimeForProjects( Constants.projectFolder, Constants.taskFolder, 
							"data/output/performanceLightPred/CRC" + CRCType + "/performance-combCRC" + CRCType + "-" + thresList[0] + "-" + 
							 thresList[1] + "-" + thresList[2] +  ".csv", thresList);
				}
			}
		}	
		*/	
	}
}