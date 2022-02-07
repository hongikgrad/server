package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Major;

import java.util.List;

public interface MajorCourseCustom {
	public List<CourseDto> findRequiredCoursesByMajor(Major studentMajor);
	public List<CourseDto> findRequiredScienceCourses();
	public List<CourseDto> findCoursesByMajor(Major studentMajor);
}
