package com.hongikgrad.graduation.dto;

import com.hongikgrad.course.dto.CourseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GraduationRequestDto {
	private Long majorId;
	private boolean isAbeek;
	private List<CourseDto> courseList;
	private Integer enterYear;
}
