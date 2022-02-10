package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.hongikgrad.course.entity.QCourse.course;

@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CourseDto> findCoursesByAbeek(String abeek) {
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
				.from(course)
				.where(course.abeek.eq(abeek))
				.fetch();
	}

	@Override
	public List<CourseDto> findMajorEnglishCourses() {
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
				.from(course)
				.where(course.number.eq("007114").or(course.number.eq("007115")))
				.limit(2)
				.fetch();
	}

	@Override
	public List<CourseDto> findEnglishCourse() {
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
				.from(course)
				.where(course.number.eq("001009"))
				.limit(1)
				.fetch();
	}

	@Override
	public List<CourseDto> findWritingCourses() {
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
				.from(course)
				.where(course.name.contains("글쓰기").and(course.abeek.eq("교양필수")))
				.limit(8)
				.fetch();
	}

	@Override
	public List<CourseDto> searchPageCourse(Pageable pageable) {
		List<CourseDto> fetch = queryFactory
				.select(
						Projections.constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit,
								course.semester
						))
				.from(course)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
		return fetch;
	}

	@Override
	public List<CourseDto> findAllCourseDto() {
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
				.from(course)
				.fetch();
	}

	@Override
	public List<CourseDto> findCoursesByNumbers(List<String> numbers) {
		return null;
	}

	@Override
	public List<CourseDto> findCoursesByNumber(String number) {
		return null;
	}
}
