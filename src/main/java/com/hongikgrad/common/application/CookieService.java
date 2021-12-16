package com.hongikgrad.common.application;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieService {
	/* 순환 참조 방지를 위해 여기선 service 사용 x */
	public String getStudentIdFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			String key = cookie.getName();
			if (key.equals("sid")) {
				return cookie.getValue();
			}
		}
		throw new NullPointerException();
	}
}
