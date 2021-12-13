package com.hongikgrad.authentication.application;

import com.hongikgrad.authentication.dto.LoginRequestDto;
import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.crawler.UserCookieCrawler;
import com.hongikgrad.common.hash.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCookieCrawler userCookieCrawler;
    private final UserRepository userRepository;
    private final SHA256 sha256;

    public void login(LoginRequestDto loginDto, HttpServletResponse response) throws IOException, AuthenticationException, NoSuchAlgorithmException {
        Map<String, String> loginData = Map.of(
                "USER_ID", loginDto.getId(),
                "PASSWD", loginDto.getPw()
        );
        Map<String, String> userAuthCookie = userCookieCrawler.getUserAuthCookie(loginData);
        String studentId = sha256.hash(loginDto.getId());
        setUserAuthCookie(userAuthCookie, response);
        setUserStudentIdCookie(studentId, response);
        saveUser(studentId);
    }

    private void saveUser(String studentId) {
        if(userRepository.existsUserByStudentId(studentId)) return;
        userRepository.save(new User(studentId));
    }

    private void setUserAuthCookie(Map<String, String> userAuthCookie, HttpServletResponse response) {
        userAuthCookie.forEach((key, value) -> {
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(60*60*24);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }

    private void setUserStudentIdCookie(String studentId, HttpServletResponse response) {
        /* 암호화된 유저 아이디 쿠키에 넣어줌 */
        Cookie cookie = new Cookie("sid", studentId);
        cookie.setMaxAge(60*60*24);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}