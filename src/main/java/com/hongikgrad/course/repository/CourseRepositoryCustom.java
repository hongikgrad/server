package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;

import java.util.List;

public interface CourseRepositoryCustom {
	public List<CourseDto> findCoursesByAbeek(String abeek);
	public List<CourseDto> findMajorEnglishCourses();
	public List<CourseDto> findEnglishCourse();
	public List<CourseDto> findWritingCourses();
}
