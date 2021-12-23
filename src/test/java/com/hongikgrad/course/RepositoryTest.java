package com.hongikgrad.course;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.common.hash.SHA256;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.*;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.course.repository.MajorRepository;
import com.hongikgrad.course.repository.UserCourseRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.hongikgrad.course.entity.QCourse.course;
import static com.hongikgrad.course.entity.QMajor.major;
import static com.hongikgrad.course.entity.QMajorCourse.*;
import static com.hongikgrad.course.entity.QUserCourse.userCourse;
import static com.querydsl.core.types.Projections.*;

@SpringBootTest
@Transactional
public class RepositoryTest {

	@Autowired
	JPAQueryFactory queryFactory;

	@Autowired
	EntityManager em;

	@Autowired
	SHA256 sha256;

	@Autowired
	MajorRepository majorRepository;

	@Autowired
	UserCourseRepository userCourseRepository;

	@Autowired
	CourseRepository courseRepository;

	public void before() {
		User user1 = new User(sha256.hash("b615500"));
	}

	@Test
	public void 유저이수과목() {
		String studentId = "b615125";
		String hashedStudentId = sha256.hash(studentId);
		List<CourseDto> courses = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(userCourse)
				.join(userCourse.course, course)
				.where(userCourse.user.studentId.eq(hashedStudentId))
				.fetch();

		for (CourseDto course : courses) {
			System.out.println(course.getName() + " " + course.getNumber() + " " + course.getAbeek());
		}
	}

	@Test
	public void 유저드래곤볼수강() {
		String studentId = "b615125";
		String hashedStudentId = sha256.hash(studentId);
		List<CourseDto> userTakenCourses = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(userCourse)
				.join(userCourse.course, course)
				.on(userCourse.user.studentId.eq(hashedStudentId))
				.fetch();

		List<CourseDto> abeek = userTakenCourses.stream()
				.filter((courseDto) -> courseDto.getAbeek().length() >= 3)
				.collect(Collectors.toList());

		List<CourseDto> major = userTakenCourses.stream()
				.filter(course -> course.getAbeek().contains("전") && course.getAbeek().length() <= 2)
				.collect(Collectors.toList());

		userTakenCourses.removeIf(courseDto ->
			(courseDto.getAbeek().length() >= 3 || (courseDto.getAbeek().contains("전") && courseDto.getAbeek().length() <= 2))
		);

		abeek.forEach(course -> System.out.println("abeek = " + course.getName() + " " + course.getAbeek()));
		major.forEach(course -> System.out.println("major = " + course.getName() + " " + course.getAbeek()));
		userTakenCourses.forEach(course -> System.out.println("all = " + course.getName() + " " + course.getAbeek()));
	}

	@Test
	public void checkElectiveTest() {
		String studentId = "b615125";
		String hashedStudentId = sha256.hash(studentId);
		List<CourseDto> userTakenCourses = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(userCourse)
				.join(userCourse.course, course)
				.on(userCourse.user.studentId.eq(hashedStudentId))
				.fetch();

		List<CourseDto> courses = userTakenCourses.stream()
				.filter((courseDto) -> courseDto.getAbeek().length() >= 3 && !courseDto.getAbeek().contains("MSC"))
				.collect(Collectors.toList());

		List<String> requiredCourses = new ArrayList<>(List.of(
				"역사와문화",
				"언어와철학",
				"사회와경제",
				"법과생활",
				"공학의이해",
				"제2외국어와한문",
				"예술과디자인",
				"영어",
				"전공기초영어",
				"글쓰기"
		));

		List<String> writingCourseNumbers = new ArrayList<>(List.of(
				"001011",
				"001012",
				"001013",
				"001014",
				"001015",
				"001020",
				"001021",
				"001022"
		));
		String englishCourseNumber = "001009";
		List<String> englishForMajorCourseNumbers = new ArrayList<>(List.of(
				"007114",
				"007115"
		));

		courses.forEach(course -> {
			String courseNumber = course.getNumber();
			String courseAbeek = course.getAbeek();
			AtomicBoolean removed = new AtomicBoolean(requiredCourses.removeIf(area -> area.equals(courseAbeek)));
			if(!removed.get()) {
				if (!removed.get() && courseNumber.equals(englishCourseNumber)) {
					requiredCourses.remove("영어");
					removed.set(true);
				}
				writingCourseNumbers.forEach(writingCourseNumber -> {
					if (!removed.get() && writingCourseNumber.equals(courseNumber)) {
						requiredCourses.remove("글쓰기");
						removed.set(true);
					}
				});
				englishForMajorCourseNumbers.forEach(englishForMajorCoursenumber -> {
					if(!removed.get() && englishForMajorCoursenumber.equals(courseNumber)) {
						requiredCourses.remove("전공기초영어");
						removed.set(true);
					}
				});
			}
		});

		courses.forEach(course -> System.out.println("abeek = " + course.getName() + " " + course.getAbeek()));
		requiredCourses.forEach(area -> System.out.println(area));
	}

	@Test
	public void majorFindTest() {
		Major major1 = new Major("전자전기공학", "공과대학");
		Major major2 = new Major("기계공학", "공과대학");
		em.persist(major1);
		em.persist(major2);

//		Major findMajor1 = majorRepository.findMajorByDeptCode("A040");
//		System.out.println("findMajor1 = " + findMajor1.getName());
//		Major findMajor2 = majorRepository.findMajorByDeptCode("A041");
//		 NPE
//		System.out.println("findMajor2 = " + findMajor2.getName());
	}

	@Test
	public void majorCourseFindTest() {
		Major findMajor = majorRepository.findMajorByNameContains("전자");
		List<CourseDto> fetch = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(majorCourse)
				.join(majorCourse.major, major)
				.on(major.name.eq(findMajor.getName()))
				.join(majorCourse.course, course)
				.fetch();

		for (CourseDto courseDto : fetch) {
			System.out.println(courseDto.getName() + " " + courseDto.getNumber() + " " + courseDto.getAbeek());
		}
	}

	@Test
	public void requiredMajorCoursesFindTest() {
		Major findMajor = majorRepository.findMajorByNameContains("전자");
		List<CourseDto> fetch = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(majorCourse)
				.join(majorCourse.major, major)
				.on(major.name.eq(findMajor.getName()))
				.join(majorCourse.course, course)
				.where(majorCourse.isRequired.eq(true))
				.fetch();

		for (CourseDto courseDto : fetch) {
			System.out.println(courseDto.getName() + " " + courseDto.getNumber() + " " + courseDto.getAbeek());
		}
	}

	@Test
	public void requiredMajorCoursesFindTest2() {
		Major findMajor = majorRepository.findMajorByNameContains("기계");
		List<CourseDto> fetch = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						)
				)
				.from(majorCourse)
				.join(majorCourse.major, major)
				.on(major.name.eq(findMajor.getName()))
				.join(majorCourse.course, course)
				.where(majorCourse.isRequired.eq(true))
				.fetch();

		for (CourseDto courseDto : fetch) {
			System.out.println(courseDto.getName() + " " + courseDto.getNumber() + " " + courseDto.getAbeek());
		}
	}

	@Test
	public void 유저가들은전공과목() {
		String username = sha256.hash("b615125");
		Major findMajor = majorRepository.findMajorByNameContains("전자");
		Major engMajor = majorRepository.findMajorByNameContains("공대");

		List<CourseDto> fetch = queryFactory
				.select(
						constructor(
								CourseDto.class,
								userCourse.course.name,
								userCourse.course.number,
								userCourse.course.abeek,
								userCourse.course.credit
						))
				.from(userCourse)
				.join(majorCourse).on(majorCourse.course.id.eq(userCourse.course.id).and(majorCourse.major.eq(findMajor).or(majorCourse.major.eq(engMajor))))
				.where(userCourse.user.studentId.eq(username))
				.fetch();

		int totalCredit = 0;
		for (CourseDto courseDto : fetch) {
			System.out.println("courseDto.getName( = " + courseDto.getName());
			totalCredit += courseDto.getCredit();
		}

		System.out.println("totalCredit = " + totalCredit);
	}

	@Test
	public void 유저가들은전공여러개() {
		String username = sha256.hash("b615125");
		Major findMajor = majorRepository.findMajorByNameContains("전자");
		Major engMajor = majorRepository.findMajorByNameContains("공대");

		List<Major> majors = new ArrayList<>(List.of(
				findMajor,
				engMajor
		));

		List<CourseDto> fetch = queryFactory
				.select(
						constructor(
								CourseDto.class,
								userCourse.course.name,
								userCourse.course.number,
								userCourse.course.abeek,
								userCourse.course.credit
						))
				.from(userCourse)
				.join(majorCourse).on(majorCourse.course.id.eq(userCourse.course.id))
				.where(userCourse.user.studentId.eq(username))
				.fetch();

		int totalCredit = 0;
		for (CourseDto courseDto : fetch) {
			System.out.println("courseDto.getName( = " + courseDto.getName());
			totalCredit += courseDto.getCredit();
		}

		System.out.println("totalCredit = " + totalCredit);
	}

	@Test
	public void 전공기초영어갖고오기() {
		List<CourseDto> fetch = queryFactory
				.select(
						constructor(
								CourseDto.class,
								course.name,
								course.number,
								course.abeek,
								course.credit
						))
				.from(course)
				.where(course.number.eq("007114").or(course.number.eq("007115")))
				.fetch();

		for (CourseDto courseDto : fetch) {
			System.out.println("courseDto.getName( = " + courseDto.getName());
		}
	}


}
