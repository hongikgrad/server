package com.hongikgrad.graduation.controller;

import com.hongikgrad.graduation.application.CourseService;
import com.hongikgrad.graduation.dto.CourseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:3000")
public class GraduationController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity courses(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                String value = cookie.getValue();
                System.out.println("value = " + value);
            }
            List<CourseResponseDto> userTakenCourses = courseService.getUserTakenCourses(request);
            return new ResponseEntity<>(userTakenCourses, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
