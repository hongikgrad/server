package com.hongikgrad.course.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CrawlingCourseDto {
	private final String name;
	private final int credit;
	private final String number;
	private final String abeek;
	private final String madeBy;
	private final String superviseBy;
	private final int year;

	@Override
	public boolean equals(Object a) {
		 CrawlingCourseDto obj = (CrawlingCourseDto) a;
		 return obj.number.equals(this.number) && obj.credit == this.credit;
	}

	@Override
	public int hashCode() {
		return (this.number + this.credit).hashCode();
	}
}
