package com.hongikgrad.major.controller;

import com.hongikgrad.authentication.application.UserService;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorCourse;
import com.hongikgrad.major.repository.MajorCourseRepository;
import com.hongikgrad.major.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class MajorCourseCRUDController {

	private final MajorCourseRepository majorCourseRepository;
	private final MajorRepository majorRepository;
	private final CourseRepository courseRepository;
	private final UserService userService;

	@GetMapping("/admin/majors/{majorId}/courses")
	public ResponseEntity majorCourseGET(HttpServletRequest request, @PathVariable("majorId") Long majorId) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
//			Major major = majorRepository.findMajorById(majorId);
			return new ResponseEntity(majorCourseRepository.findCourseDtosByMajorId(majorId), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/admin/majors/{majorId}/courses/{courseId}")
	public ResponseEntity majorCoursePOST(@PathVariable("majorId") Long majorId, @PathVariable("courseId") Long courseId, HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			Major major = majorRepository.findMajorById(majorId);
			Course course = courseRepository.findCourseById(courseId);
			if (majorCourseRepository.findMajorCourseByCourseAndMajor(course, major) == null) {
				majorCourseRepository.save(new MajorCourse(major, course));
			}
			return new ResponseEntity(majorCourseRepository.findCoursesByMajor(major), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("/admin/majors/{majorId}/courses/{courseId}")
	public ResponseEntity majorCoursePATCH(@PathVariable("majorId") Long majorId, @PathVariable("courseId") Long courseId, HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			MajorCourse majorCourse = majorCourseRepository.findMajorCourseByCourseIdAndMajorId(courseId, majorId);
			majorCourse.toggleRequired();
			majorCourseRepository.save(majorCourse);
			return new ResponseEntity(HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}


	@DeleteMapping("/admin/majors/{majorId}/courses/{courseId}")
	public ResponseEntity majorCourseDELETE(@PathVariable("majorId") Long majorId, @PathVariable("courseId") Long courseId, HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			Major major = majorRepository.findMajorById(majorId);
			Course course = courseRepository.findCourseById(courseId);
			majorCourseRepository.delete(majorCourseRepository.findMajorCourseByCourseAndMajor(course, major));
			return new ResponseEntity(HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
}
