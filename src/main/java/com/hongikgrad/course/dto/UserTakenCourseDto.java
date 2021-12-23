package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTakenCourseDto {
	private int totalCredit;
	private int totalCount;
	private List<CourseDto> courses;
}
