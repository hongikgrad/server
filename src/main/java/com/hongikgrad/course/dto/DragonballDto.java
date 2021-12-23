package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DragonballDto {
	private String area;
	private boolean isRequired;
	private List<CourseDto> courses;
}
