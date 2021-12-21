package com.hongikgrad.course.dto;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.MajorCourse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class MajorCourseListDto {
	Set<MajorCourse> majorCourses;
	Set<Course> courses;
}
