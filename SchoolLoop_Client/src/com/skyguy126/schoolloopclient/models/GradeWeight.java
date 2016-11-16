package com.skyguy126.schoolloopclient.models;

public class GradeWeight {
	private String name;
	private String percent;
	private String score;
	
	public GradeWeight(String name, String percent, String score) {
		this.name = name;
		this.percent = percent;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public String getPercent() {
		return percent;
	}

	public String getScore() {
		return score;
	}
}
