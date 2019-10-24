package com.baselineiSENSEwithCheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.data.Constants;
import com.data.TestProject;
import com.dataProcess.ProjectRankTimeSeries;
import com.dataProcess.TestProjectReader;

public class ClosePredictionwithCheck {
	public Double[] predictCloseTime ( TestProject project, String[] thresList ) {
		Double[] performance = null;
		return performance;
	}
	
	
	public void predictCloseTimeForProjects ( String folderName, String performanceFile, String[] thresList) {
		TestProjectReader reader = new TestProjectReader();
		ArrayList<TestProject> projectList = reader.loadTestProjectAndTaskList(Constants.projectFolder, Constants.taskFolder );
		projectList = ProjectRankTimeSeries.reRankProjectList(projectList);
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( new File ( performanceFile )));
			writer.write( "project" + "," + "bugsDetected" + "," + "totalBugs" + "," + "percentBugsDetected" + "," + "reportsSubmit" + "," + "totalReports" + "," 
					+ "percentReportsSubmit" + "," + "percentSavedEffort" + "," + "F1" + "," + "E1" + 
					"," + "optimalReportsSubmit" + "," + "differenceWithOptimal");
			writer.newLine();
			
			for ( int i = 0; i < projectList.size(); i++ ){
				TestProject project = projectList.get(i);
				Double[] performance = this.predictCloseTime(project, thresList);
				
				writer.write( project.getProjectName() + ",");
				for ( int j =0;  j< performance.length; j++ ) {
					writer.write( performance[j] + ",");
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
