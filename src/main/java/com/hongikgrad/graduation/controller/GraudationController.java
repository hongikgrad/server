package com.hongikgrad.graduation.controller;

import com.hongikgrad.graduation.application.GraduationService;
import com.hongikgrad.graduation.dto.RequiredCoursesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GraudationController {

    private final GraduationService graduationService;

    /* 유저가 졸업요건을 만족하는지 여부를 응답 */
    @GetMapping("users/graduation")
    public ResponseEntity userGraduation(HttpServletRequest request) {
        try {
            List<RequiredCoursesDto> requirements = graduationService.getRequirementsForGraduation(request);
            return new ResponseEntity<List<RequiredCoursesDto>>(requirements, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
