# School Loop Client

***Pulls data from a School Loop portal***

#### Features
Simple login and logout methods and easy to use data.

```java
public static void main(String[] args) {
    // Create a new client instance with your school's domain
    SClient client = new SClient("yourschool.schoolloop.com");

    // Log in
    boolean loginSuccess = client.login("username", "password");

    // Get the session cookie
    String session = client.getCookie();

    // Set a previous session cookie and check if it is still valid
    client.setCookie(session);
    boolean isSessionValid = client.isCookieValid();

    // Parse the academic data from the portal
    client.parseAcademics();

    // Get the parsed academic data
    ArrayList<Section> academicData = client.getAcademicData();

    String className = academicData.get(0).getName();
    String gradePercent = academicData.get(0).getGradePercent();

    // Invalidate the current session
    client.logout();
}
```

View `SClientTester.java` for a more complete example.

**Current Feature Set (WIP)**

- Period
- Course Name
- Teacher
- Is Published
- Grade Letter
- Grade Percent
- Number of Zeros
- Is Ap
- Progress Report
    - Assignment Name
    - Due Date
    - Score
    - Category
- Weight Scale
    - Is Weighted
    - Weight Name
    - Category Score

--------

#### Dependencies
[Jsoup](https://jsoup.org/), [OKHttp](http://square.github.io/okhttp/), [Okio](https://github.com/square/okio), [Apache HttpComponents](http://hc.apache.org/httpcomponents-client-ga/)
