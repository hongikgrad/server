package com.hongikgrad.course.dto;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.MajorCourse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CrawlingCourseListDto {
	Set<Course> courses = new HashSet<>();
	Set<MajorCourse> majorCourses = new HashSet<>();
}
