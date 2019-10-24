package com.data;

public interface Constants {
	final static String INPUT_FILE_STOP_WORD = "data/input/stopWordListBrief.txt";
	
	final static Integer FIELD_INDEX_TEST_CASE_ID = 0;
	final static Integer FIELD_INDEX_USER_ID = 1;
	final static Integer FIELD_INDEX_TEST_CASE_NAME	 = 2;
	final static Integer FIELD_INDEX_BUG_DETAIL = 9;
	final static Integer FIELD_INDEX_REPRO_STEPS = 10;
	final static Integer FIELD_INDEX_SUBMIT_TIME  = 15;
	final static Integer FIELD_BUG_TAG = 14;
	final static Integer FIELD_DUP_TAG  = 17;    //17 corresponds to R column, 18 corresponds to S column
	
	final static String projectFolder = "data/input/projects-n";              
	final static String taskFolder = "data/input/tasks-n";
	
	final static Integer EVALUATION_TIME_SERIES_BEGIN = 18;   //18;   //从下标0开始
	
	String[] attrName = { "percentBugsDetected", "percentSavedEffort", "F1" };
	String[] displayAttrName = { "%bug", "%reducedCost", "F1"};
	Integer[] attrIndexList = { 2, 6, 7};
}
