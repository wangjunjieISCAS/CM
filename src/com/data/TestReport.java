package com.data;

import java.util.Date;

public class TestReport {
	int id;
	int testCaseId;
	String testCaseName;
	String userId;
	
	String bugDetail;
	String reproSteps;
	Date submitTime;
	
	String bugTag;
	String dupTag;
	
	public TestReport( int id, int testCaseId, String testCaseName, String bugDetail, String reproSteps ) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.testCaseId = testCaseId;
		this.testCaseName = testCaseName;
		
		this.bugDetail = bugDetail;
		this.reproSteps = reproSteps;
	}

	public TestReport( int id, int testCaseId, String testCaseName, String userId, String bugDetail, String reproSteps, Date submitTime, String bugTag, String dupTag ) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.testCaseId = testCaseId;
		this.userId = userId;
		this.testCaseName = testCaseName;
		
		this.bugDetail = bugDetail;
		this.reproSteps = reproSteps;
		this.submitTime = submitTime;
		
		this.bugTag = bugTag;
		this.dupTag = dupTag;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBugDetail() {
		return bugDetail;
	}

	public void setBugDetail(String bugDetail) {
		this.bugDetail = bugDetail;
	}

	public String getReproSteps() {
		return reproSteps;
	}

	public void setReproSteps(String reproSteps) {
		this.reproSteps = reproSteps;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public String getBugTag() {
		return bugTag;
	}

	public void setBugTag(String bugTag) {
		this.bugTag = bugTag;
	}

	public String getDupTag() {
		return dupTag;
	}

	public void setDupTag(String dupTag) {
		this.dupTag = dupTag;
	}
	
}
