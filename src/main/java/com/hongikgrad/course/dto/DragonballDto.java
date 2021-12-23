package com.hongikgrad.course.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ElectiveDto {
	private String area;
	private String isRequired;
	private List<CourseDto> courses;
}
