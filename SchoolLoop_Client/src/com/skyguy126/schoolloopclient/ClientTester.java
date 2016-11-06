package com.skyguy126.schoolloopclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ClientTester {

	public static void main(String[] args) throws Exception {		
		Scanner reader = new Scanner(System.in);
		
		System.out.print("Type domain: ");
		String domain = reader.nextLine();
		
		System.out.print("Type username: ");
		String username = reader.nextLine();
		
		System.out.print("Type password: ");
		String password = reader.nextLine();
		
		reader.close();
		System.out.print("\n");
		
		Client client = new Client(domain);
		
		boolean loginSuccess = client.login(username, password);
		
		if (!client.isCookieValid()) {
			System.out.println("Invalid Session");
			return;
		}
		
		if (loginSuccess) {
			
			client.parse();
			
			ArrayList<HashMap<String, Object>> data = client.getPortalData();
			
			for (HashMap<String, Object> x : data) {
				System.out.println("\n\n\n");
				System.out.println(x.get("period") + "   " + x.get("courseName") + " - " + x.get("teacher"));
				if ((boolean) x.get("published")) {
					
					System.out.println(x.get("gradeLetter") + " " + x.get("gradePercent") + "     Zeros: " + x.get("zeros"));
					System.out.println("AP/Honors: " + x.get("isAp") + " --------- Grades Weighted: " + x.get("weighted"));
					System.out.println();
					System.out.println("-------- Assignments - Last Updated: " + x.get("lastUpdated") + " --------");
					System.out.println();
					
					@SuppressWarnings("unchecked")
					ArrayList<HashMap<String, Object>> assignmentList = (ArrayList<HashMap<String, Object>>) x.get("assignmentList");
					
					for (HashMap<String, Object> a : assignmentList) {
						System.out.println(a.get("assignmentName") + " -- Due: " + a.get("dueDate") + " -- " + a.get("score") + " -- Category: " + a.get("category"));
					}
					
					System.out.println();
					System.out.println("------- Grade Category Scale -------");
					System.out.println("Category --- Weight (optional) --- Score");
					System.out.println();
					
					@SuppressWarnings("unchecked")
					ArrayList<HashMap<String, Object>> gradeWeights = (ArrayList<HashMap<String, Object>>) x.get("gradeWeights");
					
					for (HashMap<String, Object> w : gradeWeights) {
						String weightName = (String) w.get("weightName");
						String weightScore = (String) w.get("weightScore");
						
						if ((boolean) x.get("weighted")) {
							String weightPercent = (String) w.get("weightPercent");
							System.out.println(weightName + " --- " + weightPercent + " --- " + weightScore);
						} else {
							System.out.println(weightName + " --- Not Weighted --- " + weightScore);
						}
					}
					
				} else {
					System.out.println("No Grades Published");
				}
			}

			if (client.logout() && !client.isCookieValid())
				System.out.println("\n\nLogout Success");
			else
				System.out.println("\n\nLogout Failure");
		}
	}
}
