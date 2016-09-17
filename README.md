# School Loop Client

***Pulls data from a School Loop portal***

#### Features
Contains simple login and logout methods, session storage, and easy to use parsing.

```java
public static void main(String[] args) {
    Client client = new Client("yourschool.schoolloop.com");

    ...

    //Logs in and sets the session cookie for later use
    boolean loginSuccess = client.login(username, password);

    //Gets the session cookie
    String session = client.getCookie();

    //Set a previous session cookie and check if it is still valid
    boolean isValid = client.isCookieValid();

    //Parse the data from the portal (WIP)
    client.parse();

    //Get the parsed portal data
    ArrayList<HashMap<String, Object>> portalData = client.getPortalData();

    //Invalidate the current session
    client.logout();
}
```

View `ClientTester.java` for a more complete example.

**Current Feature Set (WIP)**

- period
- courseName
- teacher
- published
- gradeLetter
- gradePercent
- zeros
- isAp
- assignmentList (Returns an `ArrayList`)
- assignmentName
- dueDate
- score
- category
- gradeWeights (Returns an `ArrayList`)
- weighted
- weightName
- weightScore

--------

#### Dependencies
[Jsoup](https://jsoup.org/), [OKHttp](http://square.github.io/okhttp/), [Okio](https://github.com/square/okio)
