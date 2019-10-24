package com.SemanticAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class WordEmbeddingTool {

	public WordEmbeddingTool() {
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<String, ArrayList<Double>> retrieveWordEmbedding ( ){
		HashMap<String, ArrayList<Double>> termEmbList = new HashMap<String, ArrayList<Double>>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("data/input/wordEmbedding/termEmbList.txt")), "utf-8"));
			String line = "";
			while ( (line = reader.readLine()) != null ){
				String[] termEmb = line.split( ":");
				String term = termEmb[0].trim();
				
				ArrayList<Double> embList = new ArrayList<Double>();
				String[] embValues = termEmb[1].split( " ");
				for ( int i =0; i < embValues.length; i++ ){
					embList.add( Double.parseDouble(embValues[i].trim() ) ) ;
				}
				termEmbList.put( term, embList );
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return termEmbList;
	}
}
