package com.hongikgrad.course.application;

import com.hongikgrad.common.crawler.CourseCrawler;
import com.hongikgrad.course.dto.*;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.exception.InvalidCookieException;
import com.hongikgrad.course.exception.InvalidDocumentException;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorCourse;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.major.repository.MajorCourseRepository;
import com.hongikgrad.major.repository.MajorHierarchyRepository;
import com.hongikgrad.major.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

	private final CourseRepository courseRepository;
	private final MajorRepository majorRepository;
	private final MajorCourseRepository majorCourseRepository;
	private final MajorHierarchyRepository majorHierarchyRepository;

	private final CourseCrawler courseCrawler;

	private List<CourseDto> allCourses;

	@PostConstruct
	public void init() {
		allCourses = courseRepository.findAllCourseDto();
	}

	public List<CourseDto> search(String keyword, String type, int enterYear) {
		switch (type) {
			case "name":
				return searchByName(keyword);
			case "number":
				return searchByNumber(keyword);
			case "major":
				return searchByMajor(keyword, enterYear);
			case "grad":
				return searchByGraduation(keyword);
			case "required":
				return searchByMajorRequired(keyword);
			default:
				return null;
		}
	}

	private List<CourseDto> searchByMajorRequired(String keyword) {
		Long majorId = Long.parseLong(keyword);
		return majorCourseRepository.findRequiredMajorCoursesByMajorId(majorId);
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
		return allCourses.stream()
				.filter(courseDto -> Objects.nonNull(courseDto.getAbeek()))
				.filter(courseDto -> courseDto.getAbeek().contains(keyword))
				.collect(Collectors.toList());
	}

	private List<CourseDto> searchByName(String name) {
		return allCourses.stream().filter(courseDto -> courseDto.getName().contains(name)).collect(Collectors.toList());
	}

	private List<CourseDto> searchByNumber(String number) {
		return allCourses.stream().filter(courseDto -> courseDto.getNumber().startsWith(number)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	List<CourseDto> searchByMajor(String major, int enterYear) {
		Long majorId = Long.parseLong(major);
		Major studentMajor = majorRepository.findMajorById(majorId);
		return majorHierarchyRepository.findAllMajorCoursesByMaster(studentMajor, enterYear);
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
		} catch (IndexOutOfBoundsException | InvalidDocumentException e) {
			e.printStackTrace();
//            return null;
		}
	}

	public long getAllCoursesCount() {
		return courseRepository.count();
	}

	public Map<String, String> extractCookie(HttpServletRequest request) throws InvalidCookieException {
		try {
			Map<String, String> extractedCookies = new HashMap<>();
			Cookie[] requestCookies = request.getCookies();

			for (Cookie cookie : requestCookies) {
				if (cookie == null) break;
				extractedCookies.put(cookie.getName(), cookie.getValue());
			}

			return extractedCookies;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidCookieException();
		}
	}

	private boolean checkProperTaken(Elements classInfo) {
		return Objects.requireNonNull(classInfo.last()).text().equals("재수강") || Objects.requireNonNull(Objects.requireNonNull(classInfo.last()).previousElementSibling()).text().equals("F");
	}


}
