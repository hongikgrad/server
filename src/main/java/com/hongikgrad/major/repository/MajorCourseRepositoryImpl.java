package com.hongikgrad.major.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.major.entity.Major;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hongikgrad.course.entity.QCourse.course;
import static com.hongikgrad.major.entity.QMajor.major;
import static com.hongikgrad.major.entity.QMajorCourse.majorCourse;
import static com.hongikgrad.major.entity.QMajorHierarchy.majorHierarchy;
import static com.querydsl.core.types.Projections.constructor;

@RequiredArgsConstructor
public class MajorCourseRepositoryImpl implements MajorCourseRepositoryCustom {

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
								course.credit,
								course.semester
						))
				.from(majorCourse)
				.join(majorCourse.major, major)
				.on(majorCourse.major.eq(studentMajor))
				.join(majorCourse.course, course)
				.where(majorCourse.isRequired.eq(true))
				.fetch();
	}

	@Override
	public List<CourseDto> findRequiredScienceCourses() {
		return queryFactory
				.select(
						Projections.constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit,
								course.semester
						)
				)
				.from(course)
				.where(course.name.contains("대학물리")
						.or(course.name.contains("대학화학")))
				.fetch();
	}

	@Override
	public List<CourseDto> findCourseDtosByMajor(Major studentMajor) {
		return queryFactory
				.select(
						Projections.constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit,
								course.semester
						))
				.from(majorCourse)
				.where(majorCourse.major.eq(studentMajor))
				.join(majorCourse.course, course)
				.fetch();
	}

	@Override
	public List<CourseDto> findCourseDtosByMajorId(Long majorId) {
		return queryFactory
				.select(
						Projections.constructor(
								CourseDto.class,
								course.id,
								course.name,
								course.number,
								course.abeek,
								course.credit,
								majorCourse.isRequired,
								course.semester
						))
				.from(majorCourse)
				.where(majorCourse.major.id.eq(majorId))
				.join(majorCourse.course, course)
				.fetch();
	}

	@Override
	public List<Course> findCoursesByMajor(Major major) {
		return queryFactory
				.select(course)
				.from(majorCourse)
				.where(majorCourse.major.eq(major))
				.join(majorCourse.course, course)
				.fetch();
	}

	@Override
	public List<Course> findCoursesByMajorId(Long majorId) {
		return queryFactory
				.select(course)
				.from(majorCourse)
				.where(majorCourse.major.id.eq(majorId))
				.join(majorCourse.course, course)
				.fetch();
	}

	@Override
	public List<CourseDto> findRequiredMajorCoursesByMajorId(Long majorId) {
		String semester1 = "20212";
		String semester2 = "20221";
		return queryFactory
				.select(
						Projections.constructor(
								CourseDto.class,
								course.id,
								course.name,
								course.number,
								course.abeek,
								course.credit,
								course.semester
						))
				.from(majorCourse)
				.join(majorCourse.course, course)
				.where(majorCourse.major.id.eq(majorId).and(majorCourse.isRequired.eq(true)).and(majorCourse.course.semester.eq(semester1).or(majorCourse.course.semester.eq(semester2))))
				.fetch();
	}

}
