package com.hongikgrad.common.crawler;

import com.hongikgrad.course.exception.InvalidDocumentException;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Component
public class UserCookieCrawler extends Crawler {

    String LOGIN_EXEC_URL = "https://ap.hongik.ac.kr/login/LoginExec3.php";
    String CLASSNET_BASE_URL = "https://cn.hongik.ac.kr";
    String CLASSNET_MAIN_URL = CLASSNET_BASE_URL + "/stud";

    public Map<String, String> getUserAuthCookie(Map<String ,String> loginData) throws InvalidDocumentException, AuthenticationException, IOException {

        Map<String, String> execPageCookie = getUserAuthCookieFromExecPage(loginData);

        Map<String, String> userAuthCookie = new HashMap<>(execPageCookie);

        Map<String, String> classnetPageCookie = getUserAuthCookieFromClassnetPage(userAuthCookie);
        userAuthCookie.putAll(classnetPageCookie);

        Map<String, String> classnetMainPageCookie = getUserAuthCookieFromClassnetMainPage(classnetPageCookie);
        userAuthCookie.putAll(classnetMainPageCookie);

        return userAuthCookie;
    }

    private Map<String, String> getUserAuthCookieFromClassnetMainPage(Map<String, String> cookies) throws InvalidDocumentException {
        return getCookieFromJsoupResponse(CLASSNET_MAIN_URL, cookies, getHeaders(), null, Connection.Method.POST);
    }

    private Map<String, String> getUserAuthCookieFromClassnetPage(Map<String ,String> cookies) throws InvalidDocumentException {
        return getCookieFromJsoupResponse(CLASSNET_BASE_URL, cookies, getHeaders(), null, Connection.Method.POST);
    }

    private Map<String, String> getUserAuthCookieFromExecPage(Map<String, String> loginInfo) throws IOException, AuthenticationException, InvalidDocumentException {
        Document loginExecPage = getJsoupResponseDocument(LOGIN_EXEC_URL, null, getHeaders(), loginInfo, Connection.Method.POST);
        Map<String, String> userAuthCookie = parseUserAuthCookie(loginExecPage);
        validateCookie(userAuthCookie);
        return userAuthCookie;
    }

    private Map<String, String> parseUserAuthCookie(Document document) {
        Map<String ,String> userAuthCookie = new HashMap<>();
        String body = document.body().html();
        StringTokenizer st = new StringTokenizer(body, "('),; ");
        while(st.hasMoreTokens()) {
            if(st.nextToken().equals("SetCookie")) {
                userAuthCookie.put(st.nextToken(), st.nextToken());
            }
        }
        return userAuthCookie;
    }

    private void validateCookie(Map<java.lang.String, java.lang.String> cookies) throws AuthenticationException {
        if(cookies.get("SUSER_ID") == null) {
            throw new AuthenticationException();
        }
    }
}

