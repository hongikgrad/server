package com.hongikgrad.graduation.dto;

import com.hongikgrad.course.dto.CourseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SubField {
	private String field;
	private List<CourseDto> courseList;
	private Integer totalCredit;
	private boolean isSatisfied;

	public void setTotalCredit(Integer totalCredit) {
		this.totalCredit = totalCredit;
	}
}
