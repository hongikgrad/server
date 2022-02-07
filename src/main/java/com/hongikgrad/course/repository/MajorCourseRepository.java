package com.hongikgrad.course.repository;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.Major;
import com.hongikgrad.course.entity.MajorCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorCourseRepository extends JpaRepository<MajorCourse, Long>, MajorCourseCustom {
	public MajorCourse findMajorCourseByCourseAndMajor(Course course, Major major);
}
