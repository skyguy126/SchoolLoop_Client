package com.skyguy126.schoolloopclient.main;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.skyguy126.schoolloopclient.models.Assignment;
import com.skyguy126.schoolloopclient.models.Attachment;
import com.skyguy126.schoolloopclient.models.GradeWeight;
import com.skyguy126.schoolloopclient.models.ProgressReportRow;
import com.skyguy126.schoolloopclient.models.Section;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SClient {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String ASSIGNMENT_URL = "/pf4/block_document/viewDocument";

	private ArrayList<Section> academicData;
	private String studentName;
	private String schoolUrl;
	private String portalBody;
	private String cookie;

	public SClient(String url) {
		this.schoolUrl = "https://" + url;
	}

	public ArrayList<Section> getAcademicData() {
		return this.academicData;
	}

	public String getCookie() {
		return this.cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getStudentName() {
		return this.studentName;
	}

	public boolean isCookieValid() throws Exception {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().get().url(this.schoolUrl + "/portal/student_home")
				.header("Cookie", this.cookie).header("User-Agent", USER_AGENT).header("Content-Type", CONTENT_TYPE)
				.build();

		Response response = client.newCall(request).execute();
		response.body().close();

		if (response.code() != 200 && response.code() != 302)
			return false;

		Response priorResponse = response.priorResponse();

		if (priorResponse != null) {
			String location = priorResponse.header("Location").trim().replace("\n", "").replace("\r", "");
			return !(location != null && location.contains("login?redirect"));
		} else {
			return true;
		}
	}

	public boolean login(String username, String password) throws Exception {
		OkHttpClient client = new OkHttpClient();
		Request loginPageRequest = new Request.Builder().get().url(this.schoolUrl + "/portal/login").build();

		Response loginPageResponse = client.newCall(loginPageRequest).execute();

		if (loginPageResponse.code() != 200) {
			loginPageResponse.body().close();
			return false;
		}

		Document loginPage = Jsoup.parse(loginPageResponse.body().string());
		String crsfid = loginPage.getElementById("form_data_id").val();

		StringBuffer cookie = new StringBuffer();

		for (String x : loginPageResponse.headers("Set-Cookie")) {
			cookie.append(x + " ");
		}

		int slidIndex = cookie.indexOf("slid");
		String slid = "slid=" + cookie.substring(cookie.indexOf("=", slidIndex) + 1, cookie.indexOf(";", slidIndex));

		int jsessionidIndex = cookie.indexOf("JSESSIONID");
		String jsessionid = "JSESSIONID="
				+ cookie.substring(cookie.indexOf("=", jsessionidIndex) + 1, cookie.indexOf(";", jsessionidIndex));

		int cfduidIndex = cookie.indexOf("__cfduid");
		String cfduid = "__cfduid="
				+ cookie.substring(cookie.indexOf("=", cfduidIndex) + 1, cookie.indexOf(";", cfduidIndex));

		String cookieString = cfduid + "; " + slid + "; " + jsessionid;

		RequestBody loginFormBody = new FormBody.Builder().add("login_name", username).add("password", password)
				.add("form_data_id", crsfid).add("event.login.x", "0").add("event.login.y", "0").build();

		Request loginRequest = new Request.Builder().post(loginFormBody)
				.url(this.schoolUrl + "/portal/login?etarget=login_form").header("Cookie", cookieString)
				.header("User-Agent", USER_AGENT).header("Content-Type", CONTENT_TYPE).build();

		Response loginResponse = client.newCall(loginRequest).execute();

		boolean loginSuccess = loginResponse.priorResponse().header("Location")
				.equals(this.schoolUrl + "/portal/student_home");

		if (loginResponse.code() != 200 || !loginSuccess) {
			loginResponse.body().close();
			return false;
		}

		this.cookie = cookieString;
		loginResponse.body().close();
		return true;
	}

	public boolean logout() throws Exception {
		Document portalSoup = Jsoup.parse(this.portalBody);
		Elements logoutid = portalSoup.select("a:contains(Logout)");
		String logoutUrl = this.schoolUrl + logoutid.attr("href");

		OkHttpClient client = new OkHttpClient();
		Request logoutRequest = new Request.Builder().get().url(logoutUrl).header("Cookie", this.cookie)
				.header("User-Agent", USER_AGENT).build();

		Response logoutResponse = client.newCall(logoutRequest).execute();
		logoutResponse.body().close();

		String returnUrl = logoutResponse.priorResponse().header("Location").trim().replace("\n", "").replace("\r", "");
		return returnUrl.equals(this.schoolUrl + "/");
	}

	public Assignment parseAssignment(String url) throws Exception {
		OkHttpClient client = new OkHttpClient();

		Request aReq = new Request.Builder().get().url(url).header("Cookie", this.cookie)
				.header("User-Agent", USER_AGENT).header("Content-Type", CONTENT_TYPE).build();

		Response aResp = client.newCall(aReq).execute();

		if (aResp.code() != 200 && aResp.code() != 302) {
			return null;
		}

		String viewResp = aResp.body().string();
		int ajaxStartIndex = viewResp.indexOf("sl.initSLAjax(\"");
		int ajaxEndIndex = viewResp.indexOf("\"", ajaxStartIndex + 15);
		String ajaxScope = viewResp.substring(ajaxStartIndex + 15, ajaxEndIndex);

		List<NameValuePair> params = URLEncodedUtils.parse(aResp.request().url().uri(),
				Charset.defaultCharset().name());

		String vdid = "";
		String groupid = "";

		for (NameValuePair pair : params) {
			if (pair.getName().equals("select_document_id"))
				vdid = pair.getValue();
			else if (pair.getName().equals("group_id"))
				groupid = pair.getValue();
		}

		String assignmentUrl = this.schoolUrl + ASSIGNMENT_URL + "?" + "classroom=t&vdid=" + vdid + "&group_id="
				+ groupid + "&ajax_scope=" + ajaxScope;

		Request assignmentRequest = new Request.Builder().get().url(assignmentUrl).header("Cookie", this.cookie)
				.header("User-Agent", USER_AGENT).header("Content-Type", CONTENT_TYPE).build();

		Response assignmentResponse = client.newCall(assignmentRequest).execute();
		Document assignmentSoup = Jsoup.parse(assignmentResponse.body().string());

		String assignmentName = assignmentSoup.select("div.sllms-content-title.big-title-22px").first().text();
		String assignmentDescription = assignmentSoup.select("div.sllms-content-body").first().text();

		Elements paramSoup = assignmentSoup.select("div.assignment-dates").first().select("div.text");

		String assignmentAssignDate = paramSoup.get(0).text();
		String assignmentDueDate = paramSoup.get(1).text();
		String assignmentPoints = (paramSoup.size() >= 3) ? paramSoup.get(2).text() : "";

		Element categorySoup = assignmentSoup.select("div.text.calendar-flyout-hide").first();
		String assignmentCategory = (categorySoup != null)
				? assignmentSoup.select("div.text.calendar-flyout-hide").first().text() : "";

		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		Elements mAttachments = assignmentSoup.select("div.attached-item");

		for (Element a : mAttachments) {
			Element mSoup = a.getElementsByTag("a").first();
			String mName = mSoup.text();
			String mUrl = mSoup.attr("href");
			attachments.add(new Attachment(mName, mUrl));
		}

		return new Assignment(assignmentName, assignmentAssignDate, assignmentDueDate, assignmentCategory,
				assignmentPoints, assignmentDescription, attachments);
	}

	public void parseAcademics() throws Exception {

		ArrayList<Section> classes = new ArrayList<Section>();

		String period = "";
		String name = "";
		String teacher = "";
		String gradeLetter = "";
		String gradePercent = "";
		String zeros = "";
		String progressReportUrl = "";
		String lastUpdated = "";

		boolean isPublished = false;
		boolean isAp = false;
		boolean isWeighted = false;
		
		ArrayList<ProgressReportRow> progressReport;
		ArrayList<GradeWeight> gradeWeights;

		OkHttpClient client = new OkHttpClient();

		Request portalRequest = new Request.Builder().get().url(this.schoolUrl + "/portal/student_home")
				.header("Cookie", this.cookie).header("User-Agent", USER_AGENT).header("Content-Type", CONTENT_TYPE)
				.build();

		Response portalResponse = client.newCall(portalRequest).execute();

		if (portalResponse.code() != 200) {
			portalResponse.body().close();
			return;
		}

		this.portalBody = portalResponse.body().string();

		Document portalSoup = Jsoup.parse(this.portalBody);

		Elements pageTitle = portalSoup.getElementsByClass("page_title");
		this.studentName = pageTitle.text().substring(0, pageTitle.text().indexOf("Portal")).trim();

		Elements academics = portalSoup.select("div.portal_tab_cont.academics_cont");
		Elements classRows = academics.first().getElementsByClass("student_row");

		for (int i = 0; i < classRows.size(); i++) {
			
			progressReport = new ArrayList<ProgressReportRow>();
			gradeWeights = new ArrayList<GradeWeight>();

			Element classMain = classRows.get(i);

			Elements periodElements = classMain.getElementsByClass("period");
			Elements courseNameElements = classMain.getElementsByClass("course");

			if (periodElements.size() > 0 && courseNameElements.size() > 0) {

				period = periodElements.text();
				name = courseNameElements.text().trim();
				teacher = classMain.select("td.teacher.co-teacher").text().trim();
				isAp = (name.toLowerCase().contains("ap") || name.toLowerCase().contains("honors")) ? true : false;

				Elements unpublished = classMain.getElementsByClass("unpublished");

				if (unpublished.size() == 0) {

					isPublished = true;
					gradeLetter = classMain.select("div.float_l.grade").text().trim();
					gradePercent = classMain.select("div.float_l.percent").text().trim();

					Elements zeroText = classMain.select("div.float_l.zeros");
					if (zeroText.first().getElementsByTag("span").size() > 0) {
						zeroText.first().getElementsByTag("span").remove();
					}

					zeros = zeroText.text().trim();
					progressReportUrl = this.schoolUrl + classMain.select("a:contains(Progress Report)").attr("href");

					Request progressReportRequest = new Request.Builder().get().header("User-Agent", USER_AGENT)
							.header("Cookie", this.cookie).url(progressReportUrl).build();

					Response progressReportResponse = client.newCall(progressReportRequest).execute();

					if (progressReportResponse.code() != 200) {
						progressReport = null;
						progressReportResponse.body().close();
					} else {
						Document progressReportSoup = Jsoup.parse(progressReportResponse.body().string());

						lastUpdated = progressReportSoup.select("span.off").text().trim();

						Element aProgressReport = progressReportSoup.getElementsByClass("general_body").first();
						Elements aProgressReportRows = aProgressReport.getElementsByTag("tr");

						for (Element row : aProgressReportRows) {
							Elements progressReportItems = row.getElementsByTag("td");

							String aName = progressReportItems.get(0).getElementsByTag("a").first().text().trim();
							String aUrl = this.schoolUrl
									+ progressReportItems.get(0).getElementsByTag("a").first().attr("href").trim();
							String aDueDate = progressReportItems.get(2).text().trim();
							String aScore = progressReportItems.get(3).text().trim();
							String[] aScoreStringParts = aScore.split(" ");

							if (aScoreStringParts.length < 7) {
								aScore = aScore.substring(aScore.indexOf("Score:") + 6, aScore.length()).trim();
							} else {
								StringBuffer aScoreString = new StringBuffer();
								aScoreString.append(aScoreStringParts[2]);
								aScoreString.append(" / ");
								aScoreString.append(aScoreStringParts[4]);
								aScoreString.append(" = ");
								aScoreString.append(aScoreStringParts[6]);

								aScore = aScoreString.toString();
							}

							Elements assignmentCategory = progressReportItems.first().select("div.float_l.padding_r5");
							assignmentCategory.first().select("a").remove();

							String aCategory = assignmentCategory.text();

							progressReport.add(new ProgressReportRow(aName, aDueDate, aCategory, aScore, aUrl));
						}

						Collections.reverse(progressReport);

						Element gradeWeightTable = progressReportSoup.select("h2:contains(Scores per Category)").first()
								.parent();
						Elements gradeWeightRows = gradeWeightTable.getElementsByTag("tr");

						isWeighted = gradeWeightTable.select("td:contains(Weight:)").size() == 1;

						for (int x = 1; x < gradeWeightRows.size(); x++) {

							Elements gradeWeightCells = gradeWeightRows.get(x).getElementsByTag("td");

							String weightName = gradeWeightCells.get(0).text();
							String weightPercent = (isWeighted) ? gradeWeightCells.get(1).text() : "";
							String weightScore = (isWeighted) ? gradeWeightCells.get(2).text()
									: gradeWeightCells.get(1).text();

							gradeWeights.add(new GradeWeight(weightName, weightPercent, weightScore));
						}
					}

				} else {
					isPublished = false;
				}

				classes.add(new Section(period, name, teacher, gradeLetter, gradePercent, zeros, progressReportUrl,
						lastUpdated, isPublished, isAp, isWeighted, progressReport, gradeWeights));
			}

			this.academicData = classes;
		}
	}
}
