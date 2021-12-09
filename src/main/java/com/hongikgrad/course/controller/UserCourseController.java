package com.hongikgrad.course.controller;

import com.hongikgrad.course.application.CourseService;
import com.hongikgrad.course.dto.CourseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /* 유저가 들은 수업들 응답 */
    @GetMapping("/users/courses")
    public ResponseEntity<List<CourseResponseDto>> getUserCourses(HttpServletRequest request) {
        try {
            List<CourseResponseDto> userTakenCourses = courseService.getUserTakenCourses(request);
            return new ResponseEntity<List<CourseResponseDto>>(userTakenCourses, HttpStatus.OK);
        } catch (IOException | NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* 유저가 들은 수업들 데이터베이스에 저장 */
    @PutMapping("/users/courses")
    public ResponseEntity putUserCourses(HttpServletRequest request) {
        try {
            courseService.saveUserTakenCourses(request);
            return new ResponseEntity<String>("저장 성공", HttpStatus.OK);
        } catch (IOException | NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* 등록되어있는 수업들 응답 */
    @GetMapping("/courses")
    public ResponseEntity getCourses() {
        return null;
    }

    /* 수업 저장 */
    @PostMapping("/courses")
    public ResponseEntity saveCourses() {
        return null;
    }
}
