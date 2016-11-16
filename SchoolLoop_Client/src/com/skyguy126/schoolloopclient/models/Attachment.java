package com.skyguy126.schoolloopclient.models;

public class Attachment {
	
	private String name;
	private String url;

	public Attachment(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
