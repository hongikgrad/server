package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CrawlingCourseListDto {
	Set<CrawlingCourseDto> courses = new HashSet<>();
	Set<String> majors = new HashSet<>();
}
