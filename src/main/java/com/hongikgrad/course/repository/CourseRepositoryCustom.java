package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseRepositoryCustom {
	List<CourseDto> findAllCourseDto();

	public List<CourseDto> findCoursesByNumbers(List<String> numbers);
	public List<CourseDto> findCoursesByNumber(String number);
	public List<CourseDto> findCoursesByAbeek(String abeek);
	public List<CourseDto> findMajorEnglishCourses();
	public List<CourseDto> findEnglishCourse();
	public List<CourseDto> findWritingCourses();

	public List<CourseDto> searchPageCourse(Pageable pageable);
}
