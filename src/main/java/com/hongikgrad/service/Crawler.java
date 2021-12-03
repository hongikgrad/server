package com.hongikgrad.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

// component?
@Component
public class Crawler {

    String LOGIN_EXEC_URL = "https://ap.hongik.ac.kr/login/LoginExec3.php";
    String CLASSNET_BASE_URL = "https://cn.hongik.ac.kr";
    String CLASSNET_MAIN_URL = CLASSNET_BASE_URL + "/stud";
    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36";

    public Map<String, String> getUserAuthCookie(Map<String ,String> loginInfo) throws IOException {

        Map<String, String> execPageCookie = getUserAuthCookieFromExecPage(loginInfo);
        if(execPageCookie == null) {
            return null;
        }

        Map<String, String> userAuthCookie = new HashMap<>(execPageCookie);

        Map<String, String> classnetPageCookie = getUserAuthCookieFromClassnetPage(userAuthCookie);
        userAuthCookie.putAll(classnetPageCookie);

        Map<String, String> classnetMainPageCookie = getUserAuthCookieFromClassnetMainPage(classnetPageCookie);
        userAuthCookie.putAll(classnetMainPageCookie);

        return userAuthCookie;
    }

    private Map<String, String> getUserAuthCookieFromClassnetMainPage(Map<String, String> cookies) throws IOException {
        return getCookieFromJsoupResponse(CLASSNET_MAIN_URL, cookies, getHeaders(), null, Connection.Method.POST);
    }

    private Map<String, String> getUserAuthCookieFromClassnetPage(Map<String ,String> cookies) throws IOException {
        return getCookieFromJsoupResponse(CLASSNET_BASE_URL, cookies, getHeaders(), null, Connection.Method.POST);
    }

    private Map<String, String> getUserAuthCookieFromExecPage(Map<String, String> loginInfo) throws IOException {
        Document loginExecPage = getJsoupResponseDocument(LOGIN_EXEC_URL, null, getHeaders(), loginInfo, Connection.Method.POST);
        return parseUserAuthCookie(loginExecPage);
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
        return validateCookie(userAuthCookie);
    }

    private Map<String, String> validateCookie(Map<java.lang.String, java.lang.String> cookies) {
        if(cookies.get("SUSER_ID") == null) {
            return null;
        }
        return cookies;
    }

    private Connection.Response getResponseByJsoup(String url, Map<String, String> cookies, Map<String, String> headers, Map<String, String> data, Connection.Method method) throws IOException {
        Connection conn = Jsoup.connect(url)
                .timeout(3000)
                .userAgent(USER_AGENT)
                .headers(headers)
                .method(method);
        if(cookies != null) conn.cookies(cookies);
        if(data != null) conn.data(data);

        Connection.Response response = null;
        try {
            response = conn.execute();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        return response;
    }

    private Map<String, String> getHeaders() {
        return Map.of(
                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Content-Type", "application/x-www-form-urlencoded",
                "Accept-Encoding", "gzip, deflate, br",
                "Origin", "https://cn.hongik.ac.kr"
        );
    }

    private Map<String, String> getHeaders(String referer) {
        Map<String, String> headers = getHeaders();
        headers.put("Referer", referer);
        return headers;
    }

    private Map<String, String> getHeaders(String referer, String origin) {
        Map<String, String> headers = getHeaders(referer);
        headers.remove("Origin");
        headers.put("Origin", origin);
        return headers;
    }

    public Map<String, String> extractCookie(HttpServletRequest request) {
        Map<String, String> cookies = new HashMap<>();
        // TODO: NULL 처리
        Cookie[] cookieArray = request.getCookies();

        for(Cookie cookie : cookieArray) {
            if(cookie == null) break;
            cookies.put(cookie.getName(), cookie.getValue());
        }

        return cookies;
    }

    public Map<String, String> getCookieFromJsoupResponse(String url, Map<String, String> cookies, Map<String, String> headers, Map<String, String> data, Connection.Method method) throws IOException {
        return getResponseByJsoup(url, cookies, headers, data, method).cookies();
    }

    public Document getJsoupResponseDocument(String url, Map<String ,String> cookies, Map<String, String> headers, Map<String, String> data, Connection.Method method) throws IOException {
        return getResponseByJsoup(url, cookies, headers, data, method).parse();
    }
}
