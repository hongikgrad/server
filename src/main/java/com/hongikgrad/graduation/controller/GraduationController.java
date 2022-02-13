package com.hongikgrad.graduation.controller;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.graduation.application.GraduationService;
import com.hongikgrad.graduation.dto.GraduationRequestDto;
import com.hongikgrad.graduation.dto.RequirementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GraduationController {

    private final GraduationService graduationService;

    /* 유저가 졸업요건을 만족하는지 여부를 응답 */
    @PostMapping("users/graduation")
    public ResponseEntity userGraduation(@RequestBody GraduationRequestDto request
    ) {
        try {
            List<CourseDto> courseList = request.getCourseList();
            for (CourseDto courseDto : courseList) {
                System.out.println(courseDto.getName());
            }
            List<RequirementDto> result = graduationService.getGraduationRequirementResult(request);
            return new ResponseEntity(result, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
