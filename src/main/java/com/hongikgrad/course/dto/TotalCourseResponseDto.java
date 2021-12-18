package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TotalCourseResponseDto {
	private int totalCredit;
	private List<CourseResponseDto> courses;
}
