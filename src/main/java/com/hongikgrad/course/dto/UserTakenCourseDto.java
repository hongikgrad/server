package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class UserTakenCourseDto {
	private int totalCredit;
	private int totalCount;
	private List<CourseDto> courses;
}
