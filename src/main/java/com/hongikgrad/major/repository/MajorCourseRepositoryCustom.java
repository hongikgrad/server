package com.hongikgrad.major.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.major.entity.Major;

import java.util.List;

public interface MajorCourseRepositoryCustom {
	public List<CourseDto> findRequiredCoursesByMajor(Major studentMajor);
	public List<CourseDto> findRequiredScienceCourses();
	public List<CourseDto> findCourseDtosByMajor(Major studentMajor);
	public List<CourseDto> findCourseDtosByMajorId(Long majorId);
	public List<Course> findCoursesByMajor(Major major);
	public List<Course> findCoursesByMajorId(Long majorId);
}
