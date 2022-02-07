package com.hongikgrad.course.repository;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.Major;

import java.util.List;

public interface UserCourseRepositoryCustom {
    List<CourseDto> findUserTakenCoursesByStudentId(String studentId);
    List<CourseDto> findUserTakenAbeekCoursesByStudentId(String studentId);
    List<CourseDto> findUserTakenMajorCoursesByStudentId(String studentId, Major studentMajor);
    boolean existsUserTakenCourse(User user, Course course);
}
