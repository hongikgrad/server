package com.hongikgrad.course.dto;

import com.hongikgrad.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseDto {
	private String name;
	private String number;
	private String abeek;
	private int credit;

	public CourseDto(String number, int credit) {
		this.number = number;
		this.credit = credit;
	}

	@Override
	public boolean equals(Object a) {
		return (a instanceof CourseDto) && (((CourseDto) a).getNumber().equals(this.getNumber())) && ((CourseDto) a).getCredit() == this.getCredit();
	}

}
