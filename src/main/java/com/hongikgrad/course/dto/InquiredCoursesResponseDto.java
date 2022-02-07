package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class InquiredCoursesResponseDto {
	private long count;
	private List<CourseDto> courses;
}
