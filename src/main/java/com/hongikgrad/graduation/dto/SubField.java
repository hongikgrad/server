package com.hongikgrad.graduation.dto;

import com.hongikgrad.course.dto.CourseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
		this.url = getCourseUrl("grad", field);
	}

	public SubField(String field, List<CourseDto> courseList, Integer totalCredit, boolean isSatisfied, String url) {
		this.field = field;
		this.courseList = courseList;
		this.totalCredit = totalCredit;
		this.isSatisfied = isSatisfied;
		this.url = url;
	}

	public void setTotalCredit(Integer totalCredit) {
		this.totalCredit = totalCredit;
	}
	private String getCourseUrl(String type, String keyword) {
		return "/courses?" + "type=" + type + "&keyword=" + keyword;
	}
}
