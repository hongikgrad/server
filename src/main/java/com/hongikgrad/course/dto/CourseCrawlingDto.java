package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseCrawlingDto {
	private String name;
	private int credit;
	private String number;
	private String abeek;

	@Override
	public boolean equals(Object a) {
		 CourseCrawlingDto obj = (CourseCrawlingDto) a;
		 return obj.number.equals(this.number) && obj.credit == this.credit;
	}

	@Override
	public int hashCode() {
		return (this.number + this.credit).hashCode();
	}
}
