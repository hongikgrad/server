package com.hongikgrad.authentication.application;

import com.hongikgrad.authentication.dto.LoginRequestDto;
import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.common.crawler.UserCookieCrawler;
import com.hongikgrad.course.exception.InvalidDocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCookieCrawler userCookieCrawler;
    private final CookieService cookieService;

    public void login(LoginRequestDto loginDto, HttpServletResponse response) throws IOException, AuthenticationException, NoSuchAlgorithmException, InvalidDocumentException {
        Map<String, String> loginData = Map.of(
                "USER_ID", loginDto.getId(),
                "PASSWD", loginDto.getPw()
        );

        Map<String, String> userAuthCookie = userCookieCrawler.getUserAuthCookie(loginData);
        setUserAuthCookie(userAuthCookie, response);
    }

    public void adminLogin(HttpServletResponse response) {
        String token = System.getenv("ADMIN_TOKEN");
        response.addCookie(makeCookie("admin", token));
    }

    public void testLogin(HttpServletResponse response) throws AuthenticationException, IOException, NoSuchAlgorithmException, InvalidDocumentException {
        String testID = System.getenv("TEST_ID");
        String testPW = System.getenv("TEST_PASSWORD");
        login(new LoginRequestDto(testID, testPW), response);
    }

    public void authenticateAdmin(HttpServletRequest request) throws AuthenticationException {
        String adminCookie = cookieService.getValueFromCookie(request, "admin");
        if(!adminCookie.equals(System.getenv("ADMIN_TOKEN"))) throw new AuthenticationException();
    }

    private void setUserAuthCookie(Map<String, String> userAuthCookie, HttpServletResponse response) {
        userAuthCookie.forEach((key, value) -> {
            Cookie cookie = makeCookie(key, value);
            response.addCookie(cookie);
        });
    }

    private Cookie makeCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*24);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

}