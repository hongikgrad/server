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

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:3000")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/auth/token")
    public ResponseEntity cookieCheck(HttpServletRequest request) {
        String ret = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String value = cookie.getValue();
            ret += value;
            ret += "\n";
        }

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping(value="/auth/token", produces = "application/json; charset=UTF-8")
    public ResponseEntity login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            userService.login(loginRequestDto, response);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}