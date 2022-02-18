package com.hongikgrad.course.dto;

import com.hongikgrad.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
	private Long id;
	private String name;
	private String number;
	private String abeek;
	private int credit;
	private boolean isRequired;
	private String semester;

	public CourseDto(String number, int credit) {
		this.number = number;
		this.credit = credit;
	}

	public CourseDto(String name, String number, String abeek, int credit, String semester) {
		this.name = name;
		this.number = number;
		this.abeek = abeek;
		this.credit = credit;
		this.isRequired = false;
		this.semester = semester;
	}

	public CourseDto(Long id, String name, String number, String abeek, int credit, String semester) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.abeek = abeek;
		this.credit = credit;
		this.semester = semester;
		this.isRequired = true;
	}

	public CourseDto(String name, String number, String abeek, int credit, boolean isRequired, String semester) {
		this.name = name;
		this.number = number;
		this.abeek = abeek;
		this.credit = credit;
		this.isRequired = isRequired;
		this.semester = semester;
	}

	@Override
	public boolean equals(Object a) {
		return (a instanceof CourseDto) && (((CourseDto) a).getNumber().equals(this.getNumber())) && ((CourseDto) a).getCredit() == this.getCredit();
	}

	@Override
	public int hashCode() {
		return (this.getNumber() + this.getCredit()).hashCode();
	}
}
