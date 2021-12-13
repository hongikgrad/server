package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseResponseDto;

import java.util.List;

public interface UserCourseRepositoryCustom {
    List<CourseResponseDto> findUserTakenCoursesByStudentId(String studentId);
}
