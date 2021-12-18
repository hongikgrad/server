package com.hongikgrad.course;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.common.hash.SHA256;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.UserCourse;
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

	@BeforeEach
	public void before() {
		User user1 = new User(sha256.hash("b615125"));
		User user2 = new User(sha256.hash("b615150"));
		User user3 = new User(sha256.hash("b615200"));
		em.persist(user1);
		em.persist(user2);
		em.persist(user3);

		List<Course> courses = new ArrayList<>();

		List<String> abeek = List.of(
			 "dragonball1",
			 "dragonball2",
			 "dragonball3",
			 "dragonball4",
			 "dragonball5",
			 "dragonball6",
			 "dragonball7",
			 "MSCmath",
			 "MSCscience",
			 "MSCcomputer",
			 "Required!!",
			 "",
			 "전선",
			 "전필",
			 "교선",
			 "일교",
			 "일선"
		);

		for(int i = 0; i < 1000; i++) {
			String courseName = "course" + Integer.toString(i);
			String courseNumber = "10" + Integer.toString(i);
			Course course = new Course(courseName, i%3+1, courseNumber, abeek.get(i%abeek.size()));
			courses.add(course);
			em.persist(course);
		}

		User[] users = {user1, user2, user3};

		for(int i = 0; i < 100; i++) {
			  int randomNumber = (int) (Math.random() * 1000);

			  UserCourse userCourse = new UserCourse(users[randomNumber % 3], courses.get(randomNumber % 1000));
			  em.persist(userCourse);
		}
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


}
