package com.hongikgrad.authentication.application;

import com.hongikgrad.authentication.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCookieCrawler userCookieCrawler;

    public void login(LoginRequestDto loginDto, HttpServletResponse response) throws IOException, AuthenticationException {
        Map<String, String> loginInfo = Map.of(
                "USER_ID", loginDto.getId(),
                "PASSWD", loginDto.getPw()
        );

        Map<String, String> userAuthCookie = userCookieCrawler.getUserAuthCookie(loginInfo);
        setUserCookieResponse(userAuthCookie, response);
    }

    private void setUserCookieResponse(Map<String, String> userAuthCookie, HttpServletResponse response) {
        userAuthCookie.forEach((key, value) -> {
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(60*60*24);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }
}