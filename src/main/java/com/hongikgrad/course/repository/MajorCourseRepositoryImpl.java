package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Major;
import com.hongikgrad.course.entity.QMajor;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hongikgrad.course.entity.QCourse.course;
import static com.hongikgrad.course.entity.QMajor.*;
import static com.hongikgrad.course.entity.QMajor.major;
import static com.hongikgrad.course.entity.QMajorCourse.majorCourse;
import static com.querydsl.core.types.Projections.constructor;

@RequiredArgsConstructor
public class MajorCourseRepositoryImpl implements MajorCourseCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CourseDto> findRequiredCoursesByMajor(Major studentMajor) {
		return queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						))
				.from(majorCourse)
				.join(majorCourse.major, major)
				.on(majorCourse.major.eq(studentMajor))
				.join(majorCourse.course, course)
				.where(majorCourse.isRequired.eq(true))
				.fetch();
	}

	@Override
	public List<CourseDto> findRequiredMSCCourse() {
		return queryFactory
				.select(
						Projections.constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(course)
				.where(course.name.contains("대학물리")
						.or(course.name.contains("대학화학")))
				.fetch();
	}
}
