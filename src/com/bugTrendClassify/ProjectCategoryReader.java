package com.bugTrendClassify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ProjectCategoryReader {

	public ProjectCategoryReader() {
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<Integer, Integer> obtainProjectCategory ( ) {
		HashMap<Integer, Integer> projectCatMap = new HashMap<Integer, Integer>();
		try {
			BufferedReader reader = new BufferedReader ( new FileReader ( new File ( "data/input/threeBugTrend.csv")));
			String line = "";
			
			while ( (line = reader.readLine() ) != null ) {
				String[] temp = line.split( ",");
				Integer projectId = Integer.parseInt( temp[0] ) ;
				Integer categoryId = Integer.parseInt( temp[1] );
				
				projectCatMap.put( projectId, categoryId );
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectCatMap;
	}
}
