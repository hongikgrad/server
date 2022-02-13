package com.hongikgrad.course.controller;

import com.hongikgrad.course.application.CourseService;
import com.hongikgrad.course.dto.CourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;

	@GetMapping("/courses")
	public ResponseEntity searchCourse(@RequestParam(value = "keyword", required = false) String keyword,
	                                     @RequestParam(value = "type", required = false) String type
	) {
		try {
			List<CourseDto> result = courseService.search(keyword, type);
			return new ResponseEntity(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping("/courses/count")
	public ResponseEntity getCount() {
		try {
			long totalCount = courseService.getAllCoursesCount();
			return new ResponseEntity<>(totalCount, HttpStatus.OK);
		} catch (Exception e) {
			return null;
		}
	}

	@GetMapping("/courses/majors/{majorCode}")
	public ResponseEntity readMajorCourses(@PathVariable("majorCode") String majorCode) {
		try {
			List<CourseDto> courses = courseService.getAllMajorCourses(majorCode);
			int totalCount = courses.size();
			return new ResponseEntity<>(courses, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
	}

	/* 시간표 사이트에서 크롤링 해서 과목들을 가져오는 컨트롤러 */
	@PostMapping("/courses")
	public ResponseEntity saveAllCourses() {
		try {
			for (int year = 2016; year <= 2021; year++) {
				courseService.getCoursesBySemester(Integer.toString(year), "1");
				courseService.getCoursesBySemester(Integer.toString(year), "2");
			}
			courseService.getCoursesBySemester("2022", "1");
			return new ResponseEntity<String>("저장 성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
		}
	}
}
