package com.hongikgrad.course.controller;

import com.hongikgrad.course.application.UserCourseService;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.dto.UserTakenCourseDto;
import com.hongikgrad.course.exception.InvalidDocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserCourseController {

    private final UserCourseService userCourseService;

    /* 유저가 들은 수업을 크롤링 한 뒤 응답 */
    @PostMapping("/users/courses")
    public ResponseEntity userCoursesPOST(@RequestBody Map<String, String> request) {
        try {
            List<CourseDto> userTakenCourses = userCourseService.loadUserTakenCourses(request);
            Integer totalCredit = userCourseService.getUserTakenTotalCredit(userTakenCourses);
            Integer totalCount = userTakenCourses.size();
            return new ResponseEntity(new UserTakenCourseDto(totalCredit, totalCount, userTakenCourses), HttpStatus.OK);
        } catch(InvalidDocumentException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users/courses/v2")
    public ResponseEntity userCoursePOSTV2(@RequestBody Map<String, String> request) {
        try {
            List<CourseDto> userTakenCourses = userCourseService.loadUserTakenCoursesV2(request);
            Integer totalCredit = userCourseService.getUserTakenTotalCredit(userTakenCourses);
            Integer totalCount = userTakenCourses.size();
            return new ResponseEntity(new UserTakenCourseDto(totalCredit, totalCount, userTakenCourses), HttpStatus.OK);
        } catch(Exception e) {
            log.warn(e.toString());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
