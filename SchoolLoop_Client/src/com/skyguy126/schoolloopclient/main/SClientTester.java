package com.skyguy126.schoolloopclient.main;

import java.util.ArrayList;
import java.util.Scanner;

import com.skyguy126.schoolloopclient.models.Assignment;
import com.skyguy126.schoolloopclient.models.CalendarRow;
import com.skyguy126.schoolloopclient.models.GradeWeight;
import com.skyguy126.schoolloopclient.models.ProgressReportRow;
import com.skyguy126.schoolloopclient.models.Section;

public class SClientTester {

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

		SClient client = new SClient(domain);

		boolean loginSuccess = client.login(username, password);
		boolean loadSuccess = client.loadPortal();

		if (!client.isCookieValid() || !loadSuccess) {
			System.out.println("Invalid Session");
			return;
		}

		if (loginSuccess) {

			System.out.println("\n\n" + client.getStudentName() + "\n");

			long saTime = System.currentTimeMillis();
			client.parseAcademics();
			System.out.println("Academic parse time: " + (System.currentTimeMillis() - saTime) + "ms");

			ArrayList<Section> classes = client.getAcademicData();

			for (Section x : classes) {
				System.out.println("\n");
				System.out.println(x.getPeriod() + "   " + x.getName() + " - " + x.getTeacher());
				if (x.isPublished()) {

					System.out.println(x.getGradeLetter() + " " + x.getGradePercent() + "     Zeros: " + x.getZeros());
					System.out.println("AP/Honors: " + x.isAp() + " --------- Grades Weighted: " + x.isWeighted());
					System.out.println();
					System.out.println("-------- Assignments - Last Updated: " + x.getLastUpdated() + " --------");
					System.out.println();

					for (ProgressReportRow a : x.getProgressReport())
						System.out.println(a.getName() + " -- Due: " + a.getDueDate() + " -- " + a.getScore()
								+ " -- Category: " + a.getCategory());

					System.out.println();
					System.out.println("------- Grade Category Scale -------");
					System.out.println("Category --- Weight (optional) --- Score");
					System.out.println();

					for (GradeWeight w : x.getGradeWeights()) {
						String weightName = w.getName();
						String weightScore = w.getScore();

						if (x.isWeighted()) {
							String weightPercent = w.getPercent();
							System.out.println(weightName + " --- " + weightPercent + " --- " + weightScore);
						} else {
							System.out.println(weightName + " --- Not Weighted --- " + weightScore);
						}
					}

				} else {
					System.out.println("No Grades Published");
				}
			}

			long scTime = System.currentTimeMillis();
			client.parseCalendar();
			System.out.println("\n\nCalendar parse time: " + (System.currentTimeMillis() - scTime) + "ms");

			ArrayList<CalendarRow> calendar = client.getCalendarData();

			System.out.println();
			System.out.println("------- Calendar -------");

			for (CalendarRow x : calendar) {
				System.out.println(x.getDay() + "     " + x.getDate());
				System.out.println("Empty: " + x.isEmpty());

				System.out.println();
				System.out.println("------- Assigments -------");

				for (Assignment a : x.getAssignments()) {
					System.out.println(a.getName() + "     Due: " + a.getDueDate());
				}

				System.out.println();
			}

			if (client.logout() && !client.isCookieValid())
				System.out.println("\n\nLogout Success");
			else
				System.out.println("\n\nLogout Failure");
		}
	}
}
