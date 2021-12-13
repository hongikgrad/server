package com.hongikgrad.course.controller;

import com.hongikgrad.course.application.CourseService;
import com.hongikgrad.course.dto.SearchCourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CourseCRUDController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity readCourses() {
        return null;
    }

    @PostMapping("/courses/abeek")
    public ResponseEntity<String> saveAbeekCourses(HttpServletResponse request, @RequestBody SearchCourseDto searchCourseDto) {
        try {
            Map<String, String> data = Map.of(
                    "p_yy", searchCourseDto.getP_yy(),
                    "p_hakgi", searchCourseDto.getP_hakgi(),
                    "p_ibhak", "2016",
                    "p_campus", "1",
                    "p_gubun", "1",
                    "p_dept", searchCourseDto.getP_dept(),
                    "p_grade", searchCourseDto.getP_grade(),
                    "p_abeek", "1"
            );
            courseService.saveAbeekCoursesFromTimeTable(data);
            return new ResponseEntity<String>("저장 성공", HttpStatus.OK);
        } catch(IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/courses/nonabeek")
    public ResponseEntity<String> saveNonAbeekCourses(HttpServletResponse request, @RequestBody SearchCourseDto searchCourseDto) {
        try {
            Map<String, String> data = Map.of(
                    "p_yy", searchCourseDto.getP_yy(),
                    "p_hakgi", searchCourseDto.getP_hakgi(),
                    "p_ibhak", "2016",
                    "p_campus", "1",
                    "p_gubun", "1",
                    "p_dept", searchCourseDto.getP_dept(),
                    "p_grade", searchCourseDto.getP_grade(),
                    "p_abeek", "1"
            );
            courseService.saveNonAbeekCoursesFromTimeTable(data);
            return new ResponseEntity<String>("저장 성공", HttpStatus.OK);
        } catch(IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/courses")
    public ResponseEntity updateCourses() {
        return null;
    }

    @DeleteMapping("/courses")
    public ResponseEntity deleteCourses() {
        return null;
    }
}
