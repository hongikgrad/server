package com.hongikgrad.major.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.graduation.dto.StudentDto;
import com.hongikgrad.major.dto.MajorDto;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.QMajor;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.hongikgrad.course.entity.QCourse.course;
import static com.hongikgrad.major.entity.QMajor.*;
import static com.hongikgrad.major.entity.QMajorCourse.majorCourse;
import static com.hongikgrad.major.entity.QMajorHierarchy.*;

@RequiredArgsConstructor
public class MajorHierarchyRepositoryImpl implements MajorHierarchyRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Major> findSlavesByMaster(MajorDto master) {
		// 순환 참조 조심
		return queryFactory
				.select(major)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.id.eq(master.getId()))
				.fetch();
	}

	@Override
	public List<CourseDto> findAllMajorCoursesByMaster(Major master) {
		List<Major> slaveList = queryFactory
				.select(majorHierarchy.slave)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.eq(master))
				.fetch();

		return queryFactory
				.select(Projections.constructor(CourseDto.class,
						course.name,
						course.number,
						course.abeek,
						course.credit,
						course.semester
				))
				.from(majorCourse)
				.join(majorCourse.major, major).on(eqAnyMajor(master, slaveList))
				.join(majorCourse.course, course).on(majorCourse.course.eq(course))
				.fetch().stream().distinct().collect(Collectors.toList());
	}



	@Override
	public List<CourseDto> findAllMajorCoursesByMaster(String majorCode) {
		List<Major> slaveList = queryFactory
				.select(majorHierarchy.slave)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.code.eq(majorCode))
				.fetch();

		List<CourseDto> majorCourseList = queryFactory
				.select(Projections.constructor(CourseDto.class,
						course.name,
						course.number,
						course.abeek,
						course.credit,
						course.semester
				))
				.from(majorCourse)
				.join(majorCourse.major, major).on(eqAnyMajor(majorCode, slaveList))
				.join(majorCourse.course, course).on(majorCourse.course.eq(course))
				.fetch();
		return majorCourseList.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<CourseDto> findAllMajorCoursesByMaster(Long majorId) {
		List<Major> slaveList = queryFactory
				.select(majorHierarchy.slave)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.id.eq(majorId))
				.fetch();

		List<CourseDto> majorCourseList = queryFactory
				.select(Projections.constructor(CourseDto.class,
						course.name,
						course.number,
						course.abeek,
						course.credit,
						course.semester
				))
				.from(majorCourse)
				.join(majorCourse.major, major).on(eqAnyMajor(majorId, slaveList))
				.join(majorCourse.course, course).on(majorCourse.course.eq(course))
				.fetch();

		return majorCourseList.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<CourseDto> findAllMajorCoursesByMaster(StudentDto student) {
		Major studentMajor = student.getMajor();

		List<Major> slaveList = queryFactory
				.select(majorHierarchy.slave)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.eq(studentMajor))
				.fetch();

		List<CourseDto> majorCourseList = queryFactory
				.select(Projections.constructor(CourseDto.class,
						course.name,
						course.number,
						course.abeek,
						course.credit,
						course.semester
				))
				.from(majorCourse)
				.join(majorCourse.major, major).on(eqAnyMajor(studentMajor, slaveList))
				.join(majorCourse.course, course).on(majorCourse.course.eq(course))
				.fetch().stream().distinct().collect(Collectors.toList());

		filterMajorCourse(studentMajor, student.getEnterYear(), majorCourseList);

		return majorCourseList;
	}

	private void filterMajorCourse(Major studentMajor, int enterYear, List<CourseDto> majorCourseList) {
		String majorCode = studentMajor.getCode();
		String college = studentMajor.getCollege();
		if(majorCode.equals("CS") && enterYear >= 20) {
			majorCourseList.removeIf(courseDto -> {
				String courseNumber = courseDto.getNumber();
				return courseNumber.equals("004174")
						|| courseNumber.equals("101810")
						|| courseNumber.equals("012305");
			});
		}

		if (college.equals("미술대학")) {
			majorCourseList.removeIf(courseDto -> courseDto.getNumber().equals("400101")
					|| courseDto.getNumber().equals("400102")
					|| courseDto.getNumber().equals("400201")
					|| courseDto.getNumber().equals("400202")
			);
		}
	}

	private BooleanExpression eqAnyMajor(Long masterId, List<Major> slaveList) {
		// 최소 1개 보장
		BooleanExpression ret = major.id.eq(masterId);
		for (Major slave : slaveList) {
			ret = ret.or(major.eq(slave));
		}
		return ret;
	}

	private BooleanExpression eqAnyMajor(Major master, List<Major> slaveList) {
		// 최소 1개 보장
		BooleanExpression ret = major.eq(master);
		for (Major slave : slaveList) {
			ret = ret.or(major.eq(slave));
		}
		return ret;
	}

	private BooleanExpression eqAnyMajor(String majorCode, List<Major> slaveList) {
		// 최소 1개 보장
		BooleanExpression ret = major.code.eq(majorCode);
		for (Major slave : slaveList) {
			ret = ret.or(major.eq(slave));
		}
		return ret;
	}

	@Override
	public List<CourseDto> findAllMajorCoursesByMaster(Major studentMajor, int enterYear) {

		List<Major> slaveList = queryFactory
				.select(majorHierarchy.slave)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.eq(studentMajor))
				.fetch();

		List<CourseDto> majorCourseList = queryFactory
				.select(Projections.constructor(CourseDto.class,
						course.name,
						course.number,
						course.abeek,
						course.credit,
						course.semester
				))
				.from(majorCourse)
				.join(majorCourse.major, major).on(eqAnyMajor(studentMajor, slaveList))
				.join(majorCourse.course, course).on(majorCourse.course.eq(course))
				.fetch().stream().distinct().collect(Collectors.toList());

		filterMajorCourse(studentMajor, enterYear, majorCourseList);

		return majorCourseList;
	}
}
