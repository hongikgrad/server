package com.hongikgrad.course.application;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.common.crawler.CourseCrawler;
import com.hongikgrad.common.crawler.UserCourseCrawler;
import com.hongikgrad.course.dto.*;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorCourse;
import com.hongikgrad.course.entity.UserCourse;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.major.repository.MajorCourseRepository;
import com.hongikgrad.major.repository.MajorRepository;
import com.hongikgrad.course.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

	private final UserCourseRepository userCourseRepository;
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final MajorRepository majorRepository;
	private final MajorCourseRepository majorCourseRepository;

	private final UserCourseCrawler userCourseCrawler;
	private final CourseCrawler courseCrawler;

	private final CookieService cookieService;

	private List<CourseDto> allCourses;

	String TAKEN_COURSE_URL = "https://cn.hongik.ac.kr/stud/P/01000/01000.jsp";

	@PostConstruct
	public void init() {
		allCourses = courseRepository.findAllCourseDto();
	}

	public List<CourseDto> search(String keyword, String type) {
		switch (type) {
			case "name":
				return searchByName(keyword);
			case "number":
				return searchByNumber(keyword);
			case "major":
				return searchByMajor(keyword);
			case "grad":
				return searchByGraduation(keyword);
			default:
				return null;
		}
	}

	@Transactional(readOnly = true)
	List<CourseDto> searchByGraduation(String keyword) {
		switch (keyword) {
			case "specializedelective":
				return courseRepository.findSpecializedElectives();
			case "writing":
				return courseRepository.findWritingCourses();
			case "english":
				return courseRepository.findEnglishCourse();
			case "majorenglish":
				return courseRepository.findMajorEnglishCourses();
		}
		return allCourses.stream().filter(courseDto -> courseDto.getAbeek().contains(keyword)).collect(Collectors.toList());
	}

	private List<CourseDto> searchByName(String name) {
		return allCourses.stream().filter(courseDto -> courseDto.getName().contains(name)).collect(Collectors.toList());
	}

	private List<CourseDto> searchByNumber(String number) {
		return allCourses.stream().filter(courseDto -> courseDto.getNumber().startsWith(number)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	List<CourseDto> searchByMajor(String major) {
		Long majorId = Long.parseLong(major);
		return majorCourseRepository.findCourseDtosByMajorId(majorId);
	}

	@Transactional(readOnly = true)
	public List<CourseDto> getAllMajorCourses(String majorCode) {
		Major studentMajor = majorRepository.findMajorByCode(majorCode.toUpperCase(Locale.ROOT));
		return majorCourseRepository.findCourseDtosByMajor(studentMajor);
	}

	/* 학기별로 개설된 강의들 가져옴 */
	public void getCoursesBySemester(String year, String hakgi) {
		try {
			System.out.println(year + " " + hakgi);
			List<String> deptList = List.of(
					"A000", "A010", "A040", "A160", "A170", "A191", "A200",
					"B010", "C010", "C020", "C030", "C040", "E000", "E010", "E020", "E030", "E040", "E050",
					"N010", "F000", "F010", "F020", "F030", "F040", "F090", "F120", "F130", "F140", "F150", "F170",
					"M000", "M020", "K010", "J010", "J020"
			);

			Map<String, String> data = new HashMap<>();
			data.put("p_ibhak", "2016");
			data.put("p_campus", "1");
			data.put("p_gubun", "1");
			data.put("p_abeek", "1");
			data.put("p_grade", "0");

			data.put("p_yy", year);
			data.put("p_hakgi", hakgi);

			/* major */
			for (String dept : deptList) {
				data.put("p_grade", "0");
				data.put("p_dept", dept);
				getCoursesFromTimeTable(year + hakgi, data);
//                majorCourses.addAll(result.getMajorCourses());
//                courses.addAll(result.getCourses());
			}

			/* elective */
			for (int j = 1; j <= 16; j++) {
				String grade = Integer.toString(j);
				data.put("p_grade", grade);
				data.put("p_dept", "0001");
				getCoursesFromTimeTable(year + hakgi, data);
//                majorCourses.addAll(result.getMajorCourses());
//                courses.addAll(result.getCourses());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 홍익대 시간표 사이트에서 과목들을 크롤링해서 가져옴 */
	private void getCoursesFromTimeTable(String semester, Map<String, String> data) throws IOException {
		try {
			CrawlingCourseListDto result = courseCrawler.getCoursesFromTimeTable(data);

			Set<Major> majors = new HashSet<>();
			Set<String> majorNames = result.getMajors();
			for (String majorName : majorNames) {
				Optional<Major> optionalMajor = majors.stream().filter(m -> m.getName().equals(majorName)).findFirst();
				Major major;
				if (optionalMajor.isPresent()) {
					major = optionalMajor.get();
				} else {
					major = majorRepository.findMajorByName(majorName);
					if (major == null) {
						major = majorRepository.save(new Major(majorName));
					}
				}
				majors.add(major);
			}

			Set<Course> courses = new HashSet<>();
			Set<MajorCourse> majorCourses = new HashSet<>();
			Set<CrawlingCourseDto> courseDtos = result.getCourses();
			for (CrawlingCourseDto courseDto : courseDtos) {
				Course course;
				course = courseRepository.findByNumberAndAndCredit(courseDto.getNumber(), courseDto.getCredit());
				if (course == null) {
					course = courseRepository.save(new Course(courseDto.getName(), courseDto.getCredit(), courseDto.getNumber(), courseDto.getAbeek(), semester));
				} else {
					// 이미 존재하는 수업 학기 갱신
					int newSemester = Integer.parseInt(semester);
					int oldSemester = 0;
					if (course.getSemester() != null) {
						oldSemester = Integer.parseInt(course.getSemester());
					}
					if (newSemester > oldSemester) {
						course.changeSemester(semester);
					}
				}
				// 전공여부 확인
				if (courseDto.getAbeek().contains("전") && !courseDto.getAbeek().contains("MSC")) {
					Major major = majors.stream().filter(m -> m.getName().equals(courseDto.getMadeBy())).findFirst().get();
					MajorCourse findMajorCourse = majorCourseRepository.findMajorCourseByCourseAndMajor(course, major);
					if (findMajorCourse == null) {
						MajorCourse majorCourse = new MajorCourse(major, course, courseDto.getAbeek().equals("전필"));
						majorCourseRepository.save(majorCourse);
					}
//                    majorCourses.add(majorCourse);
				}
			}
//            return new MajorCourseListDto(majorCourses, courses);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
//            return null;
		}
	}

	public long getAllCoursesCount() {
		return courseRepository.count();
	}

	/* 유저가 들은 수업들을 반환 */
	public List<CourseDto> loadUserTakenCourses(HttpServletRequest request) throws IOException {
		List<CourseDto> userTakenCourses = getUserTakenCoursesFromClassnet(request);
		return userTakenCourses;
	}

	public List<CourseDto> getUserTakenCoursesFromClassnet(HttpServletRequest request) throws IOException, NullPointerException {
		Document document = courseCrawler.getJsoupResponseDocument(TAKEN_COURSE_URL, extractCookie(request), courseCrawler.getHeaders(), null, Connection.Method.POST);
		List<CourseDto> courseDtoList = new ArrayList<>();
		Elements semesters = Objects.requireNonNull(document.getElementById("body")).select(".table0");
		for (Element gradeTable : semesters) {
			Elements rows = Objects.requireNonNull(gradeTable.selectFirst("tbody")).children();
			for (Element row : rows) {
				Elements classInfo = row.children();
				if (row.nextElementSibling() == null) continue;
				if (checkProperTaken(classInfo)) continue;
				String courseNumber = Objects.requireNonNull(classInfo.first()).text();
				String courseName = Objects.requireNonNull(Objects.requireNonNull(classInfo.first()).nextElementSibling()).text();
				int credit = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(classInfo.last()).previousElementSibling()).previousElementSibling().text());
				CourseDto courseDto = courseRepository.findCourseDtoByNumberAndCredit(courseNumber, credit);
				if (courseDto == null) {
					// number, credit log 남기기
					System.out.println(courseNumber);
				} else {
					courseDtoList.add(courseDto);
				}
			}
		}
		return courseDtoList;
	}

	public Map<String, String> extractCookie(HttpServletRequest request) {
		Map<String, String> extractedCookies = new HashMap<>();
		Cookie[] requestCookies = request.getCookies();

		for (Cookie cookie : requestCookies) {
			if (cookie == null) break;
			extractedCookies.put(cookie.getName(), cookie.getValue());
		}

		return extractedCookies;
	}

	private boolean checkProperTaken(Elements classInfo) {
		return Objects.requireNonNull(classInfo.last()).text().equals("재수강") || Objects.requireNonNull(Objects.requireNonNull(classInfo.last()).previousElementSibling()).text().equals("F");
	}

	public Integer getUserTakenTotalCredit(List<CourseDto> userTakenCourses) {
		Integer ret = 0;
		for (CourseDto courseDto : userTakenCourses) {
			ret += courseDto.getCredit();
		}
		return ret;
	}
}
