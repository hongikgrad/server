package com.hongikgrad.course.controller;

import com.hongikgrad.course.application.CourseService;
import com.hongikgrad.course.dto.CrawlingCourseListDto;
import com.hongikgrad.course.dto.InquiredCoursesResponseDto;
import com.hongikgrad.course.dto.CourseResponseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.MajorCourse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity readAllCourses() {
        try {
            List<CourseResponseDto> courses = courseService.getAllCourses();
            int totalCount = courses.size();
            return new ResponseEntity<InquiredCoursesResponseDto>(new InquiredCoursesResponseDto(totalCount, courses), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/courses")
    public ResponseEntity saveAllCourses() {
        List<String> deptList = List.of(
                "A000", "A010", "A040", "A160", "A170", "A191", "A200",
                "B010", "C010", "C020", "C030", "C040", "E000", "E010", "E020", "E030", "E040", "E050",
                "N010", "F000", "F010", "F020", "F030", "F040", "F090", "F120", "F130", "F140", "F150", "F170",
                "M000", "M020", "K010", "J010", "J020"
        );

        Map<String, String> data = new HashMap<>();
        data.put("p_ibhak", "2016");
        data.put("p_campus", "1");
        data.put("p_gubun", "1");
        data.put("p_abeek", "1");
        data.put("p_grade", "0");

        try {
            Set<Course> courses = new HashSet<>();
            Set<MajorCourse> majorCourses = new HashSet<>();
            for(int i = 2016; i <= 2021; i++) {
                for(int h = 1; h <= 2; h++) {
                    String year = Integer.toString(i);
                    String hakgi = Integer.toString(h);
                    data.put("p_yy", year);
                    data.put("p_hakgi", hakgi);

                    /* major */
                    for (String dept : deptList) {
                        data.put("p_grade", "0");
                        data.put("p_dept", dept);
                        CrawlingCourseListDto result = courseService.getCoursesFromTimeTable(data);
                        courses.addAll(result.getCourses());
                        majorCourses.addAll(result.getMajorCourses());
                    }

                    /* elective */
                    for (int j = 1; j <= 16; j++) {
                        String grade = Integer.toString(j);
                        data.put("p_grade", grade);
                        data.put("p_dept", "0001");
                        CrawlingCourseListDto result = courseService.getCoursesFromTimeTable(data);
                        courses.addAll(result.getCourses());
                        majorCourses.addAll(result.getMajorCourses());
                    }
                }
            }
            courseService.saveCourses(courses);
            courseService.saveMajorCourses(majorCourses);
            return new ResponseEntity<String>("저장 성공", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
