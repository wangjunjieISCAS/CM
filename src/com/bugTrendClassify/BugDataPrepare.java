package com.bugTrendClassify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.RBPredictionDynamic.RBClosePredictionCRC;
import com.SemanticAnalysis.WordSegment;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;

public class BugDataPrepare {
	HashMap<Integer, Integer> sampleSizeList;
	HashMap<Integer, Double> simThresList;
	
	public BugDataPrepare ( ){
		sampleSizeList = new HashMap<Integer, Integer>();
		simThresList = new HashMap<Integer, Double>();
		
		try {
			BufferedReader reader = new BufferedReader ( new FileReader ( new File ( "data/input/bestPara-MhCH.csv")));
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
	
	public void prepareBugData ( TestProject project){
		ArrayList<Integer> uniqueBugTagList = new ArrayList<Integer>();
		ArrayList<TestReport> reportList = project.getTestReportsInProj();
		
		HashSet<String> noDupBug = new HashSet<String>();
		for ( int i =0; i < reportList.size(); i++ ){
			TestReport report = reportList.get( i );
			if ( report.getBugTag().equals("审核不通过")){
				uniqueBugTagList.add( 0 );
			}else{
				if ( noDupBug.contains( report.getDupTag() )){
					uniqueBugTagList.add( 0 );
				}else{
					uniqueBugTagList.add( 1 );
					noDupBug.add( report.getDupTag() );
				}
			}
		}
		
		String[] temp = project.getProjectName().split("-");
		Integer index = Integer.parseInt( temp[0] );
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( "data/input/bugData/" + index + ".csv") );
			for ( int i =0; i < uniqueBugTagList.size(); i++ ){
				writer.write( i+1 + "," + uniqueBugTagList.get(i));
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//之前的思路是首先根据prediction的情况，生成predicted bug arrival curve，然后存储，然后统一使用ThreeBugTrendClassify进行分类
	//后来根据基于dynamic rules，得到的close time，有%多少的缺陷发现了。小于80%为第三类，80%-99%为第二类，100%为第一类
	public void prepareBugDataWithPredictedBugAndDupTag ( TestProject project ){
		RBClosePredictionCRC basePrediction = new RBClosePredictionCRC( );
		System.out.println ( project.getProjectName() );
		
		String[] temp = project.getProjectName().split("-");
		Integer index = Integer.parseInt( temp[0] );
		if ( !sampleSizeList.containsKey( index )){
			return;
		}
		Integer sampleSize = sampleSizeList.get( index );
		Double simThres = simThresList.get( index );
		
		ArrayList<Integer> reportPredDupTag = new ArrayList<Integer>();
		//对于第0个报告，dupTag为0；第1个报告，计算和第0个的相似性，如果相似，dupTag为0，否则dupTag为1；第i个报告，计算和前面各个dupTag组中报告的相似性，如果相似，即为给组的编号；否则新开一个组
		
		ArrayList<HashMap<String, Integer>> histReportTermsList = new ArrayList<HashMap<String, Integer>>();
		ArrayList<HashMap<String, Integer>> curCaptureReportTermsList = new ArrayList<HashMap<String, Integer>>();
		
		ArrayList<Integer> uniqueBugTagList = new ArrayList<Integer>();
		Integer curGroupNum = 0;
		for ( int i = 0 ; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			HashMap<String, Integer> reportTerms = WordSegment.obtainUniqueTermForReport( report );
			curCaptureReportTermsList.add( reportTerms );
			
			if ( (i == 0 || (i+1) % sampleSize !=0) && i != project.getTestReportsInProj().size()-1  )
				continue;
			
			for ( int j =0; j < curCaptureReportTermsList.size(); j++ ){
				HashMap<String, Integer> curReportTerms = curCaptureReportTermsList.get( j );
				
				if ( curReportTerms.size() <= 1 ){     //该规则作为是否为bug的判断
					uniqueBugTagList.add( 0 );
					continue;
				}
				
				Integer dupTag = basePrediction.obtainDupTagInfo(curReportTerms, histReportTermsList, reportPredDupTag, simThres);
				if ( dupTag == -1 ) {
					dupTag = ++ curGroupNum;
					
					uniqueBugTagList.add( 1 );
				}
				else{
					uniqueBugTagList.add( 0 );
				}
				
				reportPredDupTag.add( dupTag );
				histReportTermsList.add( curReportTerms );
			}	
			curCaptureReportTermsList.clear();          //一次capture处理完之后，将curCaptureReports清空
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter ( "data/input/bugData-Predicted/" + index + ".csv") );
			for ( int i =0; i < uniqueBugTagList.size(); i++ ){
				writer.write( i + "," + uniqueBugTagList.get(i ));
				//System.out.println ( "output ------ " + i + " " + uniqueBugTagList.get(i)); 
				
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main ( String[] args){
		BugDataPrepare prepareTool = new BugDataPrepare();
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectAndTaskList(Constants.projectFolder, Constants.taskFolder );
		for ( int i =0; i < projectList.size(); i++ ){
			//prepareTool.prepareBugData( projectList.get( i ));
			
			prepareTool.prepareBugDataWithPredictedBugAndDupTag( projectList.get(i) );
		}
	}
}
