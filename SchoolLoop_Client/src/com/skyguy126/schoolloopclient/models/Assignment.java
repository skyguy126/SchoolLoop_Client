package com.skyguy126.schoolloopclient.models;

import java.util.ArrayList;

public class Assignment {

	private String name;
	private String assignDate;
	private String dueDate;
	private String category;
	private String points;
	private String description;

	private ArrayList<Attachment> attachments;

	public Assignment(String name, String assignDate, String dueDate, String category, String points, String description,
			ArrayList<Attachment> attachments) {
		this.name = name;
		this.assignDate = assignDate;
		this.dueDate = dueDate;
		this.category = category;
		this.points = points;
		this.description = description;
		this.attachments = attachments;
	}

	public String getName() {
		return name;
	}
	
	public String getAssignDate() {
		return assignDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public String getCategory() {
		return category;
	}

	public String getPoints() {
		return points;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}

}
