package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;

import java.util.List;

public interface UserCourseRepositoryCustom {
    List<CourseDto> findUserTakenCoursesByStudentId(String studentId);
    List<CourseDto> findUserTakenAbeekCoursesByStudentId(String studentId);
}
