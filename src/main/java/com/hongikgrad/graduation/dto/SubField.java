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
	private String url;

	public SubField(String field, List<CourseDto> courseList, Integer totalCredit, boolean isSatisfied) {
		this.field = field;
		this.courseList = courseList;
		this.totalCredit = totalCredit;
		this.isSatisfied = isSatisfied;
		this.url = getCourseUrl("cat", field);
	}

	public void setTotalCredit(Integer totalCredit) {
		this.totalCredit = totalCredit;
	}
	private String getCourseUrl(String command, String keyword) {
		return "/courses?" + "command=" + command + "&keyword=" + keyword;
	}
}
