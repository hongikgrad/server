package com.hongikgrad.course.controller;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseCRUDController {

	private final CourseRepository courseRepository;

	@GetMapping("/admin/courses")
	public ResponseEntity adminCourseGET(@RequestParam(value = "keyword", required = false) String keyword) {
		try {
			List<Course> findCourseList = courseRepository.findCourseByNameContains(keyword);
			return new ResponseEntity(findCourseList, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("/admin/courses")
	public ResponseEntity adminCoursePATCH() {
		return null;
	}


}
