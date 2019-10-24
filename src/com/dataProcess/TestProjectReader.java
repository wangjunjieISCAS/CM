package com.dataProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.csvreader.CsvReader;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.data.TestTask;

/*
 * 从文件中读取测试报告，并作为一个testProject存储
 */
public class TestProjectReader {

	public TestProjectReader() {
		// TODO Auto-generated constructor stub
	}
	
	public String transferString ( String str ){
		String result = str;
		result = result.replaceAll( "\r\n", " " );
		result = result.replaceAll( "\r", " " );
		result = result.replaceAll( "\n", " " );
		
		return result;
	}
	
	public TestProject loadTestProject ( String fileName ){
		int sep = fileName.lastIndexOf( "/");
		String projectName = fileName.substring( sep+1, fileName.length() );
		System.out.println ( projectName );
		
		TestProject testProject = new TestProject ( projectName );
		
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
		SimpleDateFormat formatCon = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			
			CsvReader reader = new CsvReader( br, ',');
			
			// 跳过表头   如果需要表头的话，不要写这句。  
			reader.readHeaders(); 
			//逐行读入除表头的数据      
			int index = 0;
	        while ( reader.readRecord() ){
	        	String[] temp = reader.getValues();
	        	
	        	int id = index ++;
	        	int testCaseId = Integer.parseInt( temp[Constants.FIELD_INDEX_TEST_CASE_ID]);
	        	String testCaseName = this.transferString( temp[Constants.FIELD_INDEX_TEST_CASE_NAME] );
	        	String userId = this.transferString( temp[Constants.FIELD_INDEX_USER_ID]);
	        	String bugDetail = this.transferString( temp[Constants.FIELD_INDEX_BUG_DETAIL] ) ;
	        	String reproSteps = this.transferString( temp[Constants.FIELD_INDEX_REPRO_STEPS] );
	        	
	        	String time = this.transferString( temp[Constants.FIELD_INDEX_SUBMIT_TIME]);
	        	
	        	Date submitTime  = null;
	        	if ( time.contains( "-")) {
	        		submitTime = formatCon.parse( time );
	        	}
	        	else {
	        		submitTime = formatLine.parse( time );
	        	}
	        	
	        	String bugTag = temp[Constants.FIELD_BUG_TAG].trim();
	        	String dupTag = temp[Constants.FIELD_DUP_TAG].trim();
	        	if ( bugTag.equals("待审核")){
	        		bugTag = "审核通过";
	        	}
	        	TestReport report = new TestReport ( id, testCaseId, testCaseName, userId, bugDetail, reproSteps, submitTime, bugTag, dupTag );
	        	
	        	testProject.getTestReportsInProj().add( report );
	        }
			
	        reader.close();
			//System.out.println ( "testProject size: " + testProject.getTestReportsInProj().size()  );
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return testProject;
	}
	
	
	public ArrayList<TestProject> loadTestProjectList ( String projectFolder ) {
		ArrayList<TestProject> projectList = new ArrayList<TestProject>();
		
		TestProjectReader reader = new TestProjectReader();
		File projectsFolder = new File ( projectFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String fileName = projectFolder + "/" + projectFileList[i];
				
				TestProject project = reader.loadTestProject( fileName );
				projectList.add( project );
			}
		}
		return projectList;
	}
	
	public TestProject loadTestProjectAndTask ( String fileName, String taskFileName ){
		TestProject project = this.loadTestProject( fileName );
		ArrayList<String> taskDes = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( taskFileName )));
			String line = "";
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( " ");
				
				for ( int i =0; i < temp.length; i++ ) {
					taskDes.add( temp[i] );
				}
			}
			
			TestTask task = new TestTask ( taskDes );
			project.setTestTask( task );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return project;
	}
	
	
	public ArrayList<TestProject> loadTestProjectAndTaskList ( String projectFolder, String taskFolder ){
		ArrayList<TestProject> projectList = new ArrayList<TestProject>();
		
		File projectsFolder = new File ( projectFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String projectFileName = projectFolder + "/" + projectFileList[i];
				
				String projectName = projectFileList[i].substring( 0, projectFileList[i].length() - 4);
				String taskFileName = taskFolder + "/" + projectName + ".txt";
				
				TestProject project = this.loadTestProjectAndTask( projectFileName, taskFileName );
				projectList.add( project );
			}				
		}			
		
		return projectList;
	}
}
