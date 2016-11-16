package com.skyguy126.schoolloopclient.models;

import java.util.ArrayList;

public class Section {
	private String period;
	private String name;
	private String teacher;
	private String gradeLetter;
	private String gradePercent;
	private String zeros;
	private String progressReportUrl;
	private String lastUpdated;
	
	private boolean isPublished;
	private boolean isAp;
	private boolean isWeighted;
	
	private ArrayList<ProgressReportRow> progressReport;
	private ArrayList<GradeWeight> gradeWeights;
	
	public Section(String period, String name, String teacher, String gradeLetter, String gradePercent, String zeros,
			String progressReportUrl, String lastUpdated, boolean isPublished, boolean isAp, boolean isWeighted,
			ArrayList<ProgressReportRow> progressReport, ArrayList<GradeWeight> gradeWeights) {
		this.period = period;
		this.name = name;
		this.teacher = teacher;
		this.gradeLetter = gradeLetter;
		this.gradePercent = gradePercent;
		this.zeros = zeros;
		this.progressReportUrl = progressReportUrl;
		this.lastUpdated = lastUpdated;
		this.isPublished = isPublished;
		this.isAp = isAp;
		this.isWeighted = isWeighted;
		this.progressReport = progressReport;
		this.gradeWeights = gradeWeights;
	}

	public String getPeriod() {
		return period;
	}

	public String getName() {
		return name;
	}

	public String getTeacher() {
		return teacher;
	}

	public String getGradeLetter() {
		return gradeLetter;
	}

	public String getGradePercent() {
		return gradePercent;
	}

	public String getZeros() {
		return zeros;
	}

	public String getProgressReportUrl() {
		return progressReportUrl;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public boolean isAp() {
		return isAp;
	}

	public boolean isWeighted() {
		return isWeighted;
	}

	public ArrayList<ProgressReportRow> getProgressReport() {
		return progressReport;
	}

	public ArrayList<GradeWeight> getGradeWeights() {
		return gradeWeights;
	}
}
