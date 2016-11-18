package com.skyguy126.schoolloopclient.models;

import java.util.ArrayList;

public class CalendarRow {
	
	private String day;
	private String date;
	
	private boolean isEmpty;
	
	private ArrayList<Assignment> assignments;

	public CalendarRow(String day, String date, boolean isEmpty, ArrayList<Assignment> assignments) {
		this.day = day;
		this.date = date;
		this.isEmpty = isEmpty;
		this.assignments = assignments;
	}

	public String getDay() {
		return day;
	}

	public String getDate() {
		return date;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public ArrayList<Assignment> getAssignments() {
		return assignments;
	}	
}
