package com.hongikgrad.authentication.controller;

import com.hongikgrad.authentication.application.UserService;
import com.hongikgrad.authentication.dto.LoginRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/auth/token")
    public ResponseEntity<String> cookieCheck(HttpServletRequest request) {
        try {
            StringBuilder body = new StringBuilder();
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                body.append(cookie.getValue());
                body.append("\n");
            }
            return new ResponseEntity<String>(body.toString(), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/auth/token", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            userService.login(loginRequestDto, response);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<String>("Invalid Student ID", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value="/auth/token")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        return null;
    }
}