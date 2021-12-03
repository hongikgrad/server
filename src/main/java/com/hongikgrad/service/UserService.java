package com.hongikgrad.service;

import com.hongikgrad.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Crawler crawler;

    public Boolean login(LoginDto loginDto, HttpServletResponse response) throws IOException {
        Map<String, String> loginInfo = Map.of(
                "USER_ID", loginDto.getId(),
                "PASSWD", loginDto.getPw()
        );

        Map<String, String> userAuthCookie = crawler.getUserAuthCookie(loginInfo);
        if(userAuthCookie == null) {
            return false;
        }
        getUserCookieResponse(userAuthCookie, response);
        return true;
    }

    private HttpServletResponse getUserCookieResponse(Map<String, String> userAuthCookie, HttpServletResponse response) {
        userAuthCookie.forEach((key, value) -> {
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(60*60*24);
            response.addCookie(cookie);
        });
        return response;
    }
}