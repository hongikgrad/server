package com.hongikgrad.graduation.dto;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Major;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StudentDto {
	private int enterYear;
	private Major major;
	private boolean isAbeek;
	private List<CourseDto> takenCourses;
}
