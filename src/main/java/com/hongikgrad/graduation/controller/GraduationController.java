package com.hongikgrad.graduation.controller;

import com.hongikgrad.graduation.application.GraduationService;
import com.hongikgrad.graduation.dto.RequirementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GraduationController {

    private final GraduationService graduationService;

    /* 유저가 졸업요건을 만족하는지 여부를 응답 */
    @GetMapping("users/graduation")
    public ResponseEntity userGraduation(HttpServletRequest request) {
        try {
            List<RequirementDto> result = graduationService.getGraduationRequirementTestResult(request);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
