package com.hongikgrad.major.repository;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorCourseRepository extends JpaRepository<MajorCourse, Long>, MajorCourseRepositoryCustom {
	public MajorCourse findMajorCourseByCourseAndMajor(Course course, Major major);
	public Boolean existsMajorCourseByMajorAndCourse(Major major, Course course);
}
