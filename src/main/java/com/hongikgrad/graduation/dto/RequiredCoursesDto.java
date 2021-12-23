package com.hongikgrad.graduation.dto;

import com.hongikgrad.course.dto.CourseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequiredCoursesDto {
	private String area;
	private Object requirements;
}
