package com.hongikgrad.authentication.application;

import com.hongikgrad.authentication.dto.LoginRequestDto;
import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.common.crawler.UserCookieCrawler;
import com.hongikgrad.common.hash.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCookieCrawler userCookieCrawler;
    private final UserRepository userRepository;
    private final SHA256 sha256;
    private final CookieService cookieService;

    public void login(LoginRequestDto loginDto, HttpServletResponse response) throws IOException, AuthenticationException, NoSuchAlgorithmException {
        Map<String, String> loginData = Map.of(
                "USER_ID", loginDto.getId().toLowerCase(Locale.ROOT),
                "PASSWD", loginDto.getPw()
        );

        Map<String, String> userAuthCookie = userCookieCrawler.getUserAuthCookie(loginData);
        String studentId = sha256.hash(loginDto.getId());
        String studentEnterYear = convertStudentIdToEnterYear(loginDto.getId());

        setUserAuthCookie(userAuthCookie, response);
        setUserStudentIdCookie(studentId, response);
        setUserEnterYearCookie(studentEnterYear, response);
//        saveUser(studentId);
    }

    public void adminLogin(HttpServletResponse response) {
        String token = System.getenv("ADMIN_TOKEN");
        response.addCookie(makeCookie("admin", token));
    }

    public void testLogin(HttpServletResponse response) throws AuthenticationException, IOException, NoSuchAlgorithmException {
        String testID = System.getenv("TEST_ID");
        String testPW = System.getenv("TEST_PASSWORD");
        login(new LoginRequestDto(testID, testPW), response);
    }

    public void authenticateAdmin(HttpServletRequest request) throws AuthenticationException {
        String adminCookie = cookieService.getValueFromCookie(request, "admin");
        if(!adminCookie.equals(System.getenv("ADMIN_TOKEN"))) throw new AuthenticationException();
    }

    private void saveUser(String studentId) {
        if(!userRepository.existsUserByStudentId(studentId)) {
            userRepository.save(new User(studentId));
        }
    }

    private void setUserAuthCookie(Map<String, String> userAuthCookie, HttpServletResponse response) {
        userAuthCookie.forEach((key, value) -> {
            Cookie cookie = makeCookie(key, value);
            response.addCookie(cookie);
        });
    }

    private void setUserStudentIdCookie(String studentId, HttpServletResponse response) {
        /* 암호화된 유저 아이디 쿠키에 넣어줌 */
        Cookie cookie = makeCookie("sid", studentId);
        response.addCookie(cookie);
    }

    private void setUserEnterYearCookie(String studentEnterYear, HttpServletResponse response) {
        Cookie cookie = makeCookie("enter", studentEnterYear);
        response.addCookie(cookie);
    }

    private String convertStudentIdToEnterYear(String studentId) {
        return studentId.substring(0, 2).replace(studentId.charAt(0), (char) (studentId.charAt(0) - 'a'+'0'));
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