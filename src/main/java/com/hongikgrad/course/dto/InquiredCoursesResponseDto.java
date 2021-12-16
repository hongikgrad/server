package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class InquiredCoursesResponseDto {
	private int count;
	private List<CourseResponseDto> courses;
}
