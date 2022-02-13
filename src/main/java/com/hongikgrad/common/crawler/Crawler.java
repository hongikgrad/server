package com.hongikgrad.common.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Crawler {

    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36";

    private Connection.Response getResponseByJsoup(String url, Map<String, String> cookies, Map<String, String> headers, Map<String, String> data, Connection.Method method) {
        try {
            Connection conn = Jsoup.connect(url)
                    .timeout(1000*20)
                    .userAgent(USER_AGENT)
                    .headers(headers)
                    .method(method);
            if (cookies != null) conn.cookies(cookies);
            if (data != null) conn.data(data);
            return conn.execute();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getHeaders() {
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
        Map<String, String> extractedCookies = new HashMap<>();
        Cookie[] requestCookies = request.getCookies();

        for (Cookie cookie : requestCookies) {
            if (cookie == null) break;
            extractedCookies.put(cookie.getName(), cookie.getValue());
        }

        return extractedCookies;
    }

    public Map<String, String> getCookieFromJsoupResponse(String url, Map<String, String> cookies, Map<String, String> headers, Map<String, String> data, Connection.Method method) throws IOException {
        return getResponseByJsoup(url, cookies, headers, data, method).cookies();
    }

    public Document getJsoupResponseDocument(String url, Map<String, String> cookies, Map<String, String> headers, Map<String, String> data, Connection.Method method) throws IOException {
        return getResponseByJsoup(url, cookies, headers, data, method).parse();
    }
}
