package com.skyguy126.schoolloopclient.models;

public class ProgressReportRow {
	private String name;
	private String dueDate;
	private String category;
	private String score;
	private String url;
	
	public ProgressReportRow(String name, String dueDate, String category, String score, String url) {
		this.name = name;
		this.dueDate = dueDate;
		this.category = category;
		this.score = score;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getDueDate() {
		return dueDate;
	}

	public String getCategory() {
		return category;
	}

	public String getScore() {
		return score;
	}

	public String getUrl() {
		return url;
	}
}
