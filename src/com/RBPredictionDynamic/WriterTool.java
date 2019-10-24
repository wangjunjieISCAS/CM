package com.RBPredictionDynamic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.data.TestProject;
import com.data.TestReport;

public class WriterTool {
	
	public void outputPredictionDetails ( ArrayList<Integer[]> CRCResultHistory, String predDetailsOutFile) {
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter(new File ( predDetailsOutFile )));
			writer.write( "N1" + "," + "N2" + "," + "N3" + "," + "currentDetectedBugs" + "," + "endReport" + "," + "groundTruthTotalBugs" );
			writer.newLine();
			
			for ( int i =0; i < CRCResultHistory.size(); i++ ){
				Integer[] result = CRCResultHistory.get( i );
				writer.write( result[0] + "," + result[1] + "," + result[2] + "," + result[3] + "," + result[4] + "," + result[5] );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void outputPredictionDupTag ( TestProject project, ArrayList<Integer> bugTagList, ArrayList<Integer> dupTagList, String outputFile ){
		String[] titles = { "case编号", "用户id", "case标题", "地域", "机型", "操作系统", "网络环境", "运营商", "ROM信息", "bug详情",
				"复现步骤", "截图", "是否未知", "优先级", "审核状态", "提交时间", " ", "重复情况" };
		
		ArrayList<String[]> projectDetails = new ArrayList<String[]>();
		projectDetails.add( titles );
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ){
			//System.out.println( i + " " + dupTagList.size() );
			if ( i >= dupTagList.size()  ){
				break;
			}			
			
			TestReport report = project.getTestReportsInProj().get( i );
			
			String[] content = new String[titles.length];
			for ( int j =0; j < content.length; j++ ){
				content[j] = " ";
			}
			
			content[0] = ((Integer)report.getTestCaseId()).toString();
			content[1] = report.getUserId();
			content[2] = report.getTestCaseName();
			content[9] = report.getBugDetail().replaceAll(",", ".");
			content[10] = report.getReproSteps().replaceAll(",", ".");
			
			String bugTag = "审核通过";
			if ( bugTagList.get( i) !=1 ){
				bugTag = "审核不通过";
			}
			content[14] = bugTag;
			
			content[15] = "2000/01/01 00:00";			
			String dupTag = dupTagList.get( i).toString();
			content[17] = dupTag;
			
			projectDetails.add( content );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter ( new FileWriter (new File ( outputFile )));
		
			for ( int i =0; i < projectDetails.size(); i++ ){
				String[] details = projectDetails.get( i );
				for ( int j =0; j < details.length; j++  ){
					writer.write( details[j] + ",");
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
}
