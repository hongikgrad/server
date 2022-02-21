package com.hongikgrad.course.controller;

import com.hongikgrad.course.application.CourseService;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.dto.UserTakenCourseDto;
import com.hongikgrad.course.exception.InvalidDocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserCourseController {

    private final CourseService courseService;

    /* 유저가 들은 수업을 크롤링 한 뒤 응답 */
    @PostMapping("/users/courses")
    public ResponseEntity userCoursesPOST(@RequestBody Map<String, String> request) {
        try {
            List<CourseDto> userTakenCourses = courseService.loadUserTakenCourses(request);
            Integer totalCredit = courseService.getUserTakenTotalCredit(userTakenCourses);
            Integer totalCount = userTakenCourses.size();
            return new ResponseEntity(new UserTakenCourseDto(totalCredit, totalCount, userTakenCourses), HttpStatus.OK);
        } catch(InvalidDocumentException e) {
            return new ResponseEntity(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
