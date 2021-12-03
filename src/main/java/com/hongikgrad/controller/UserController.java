package com.hongikgrad.controller;

import com.hongikgrad.dto.LoginDto;
import com.hongikgrad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/auth/token")
    public String cookieCheck(HttpServletRequest request) {
        String ret = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String value = cookie.getValue();
            ret += value;
            ret += "\n";
        }
        return ret;
    }

    @PostMapping(value="/auth/token", produces = "application/json; charset=UTF-8")
    public Boolean login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return userService.login(loginDto, response);
    }
}