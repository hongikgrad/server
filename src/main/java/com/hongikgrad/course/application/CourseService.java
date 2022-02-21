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

	String TAKEN_COURSE_URL = "https://cn.hongik.ac.kr/stud/P/01000/01000.jsp";

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

	/* 유저가 들은 수업들을 반환 */
	public List<CourseDto> loadUserTakenCourses(Map<String, String> request) throws InvalidCookieException, InvalidDocumentException {
		return getUserTakenCoursesFromClassnetV2(request);
	}

	public List<CourseDto> getUserTakenCoursesFromClassnetV2(Map<String, String> request) throws InvalidDocumentException, InvalidCookieException {
		List<CourseDto> courseDtoList = new ArrayList<>();
		Document userTakenCourseDocument = courseCrawler.getJsoupResponseDocument(TAKEN_COURSE_URL, request, courseCrawler.getHeaders(), null, Connection.Method.POST);
		Element body = userTakenCourseDocument.getElementById("body");
		Elements semesterTableList = getValidTableElements(body);

		for (Element table : semesterTableList) {
			Element tableHead = table.selectFirst("thead");
			int courseNumberIndex = getCourseNumberIndex(tableHead);
			int courseCreditIndex = getCourseCreditIndex(tableHead);

			Element tableBody = table.selectFirst("tbody");
			if(tableBody == null) continue;
			Elements tableRows = tableBody.children();
			extractCourseData(tableRows, courseDtoList, courseNumberIndex, courseCreditIndex);
		}
		return courseDtoList;
	}

	private int getColumnIndex(Element tableHead, String columnText) {
		if(tableHead == null) return -1;
		Element row = tableHead.child(0);
		for(int index = 0; index < row.childrenSize(); index++) {
			String text = row.child(index).text();
			if (text.equals(columnText)) {
				return index;
			}
		}
		return 0;
	}

	private int getCourseCreditIndex(Element tableHead) {
		return getColumnIndex(tableHead, "학점");
	}

	private int getCourseNumberIndex(Element tableHead) {
		return getColumnIndex(tableHead, "학수번호");
	}

	private void extractCourseData(Elements tableRows, List<CourseDto> courseDtoList, int courseNumberIndex, int courseCreditIndex) {
		try {
			for (Element tableRow : tableRows) {
				if (!isCourseInfoRow(tableRow)) continue;
				if (isRetakenCourse(tableRow)) continue;
				Element courseNumberElement = tableRow.child(courseNumberIndex);
				Element courseCreditElement = tableRow.child(courseCreditIndex);
				String courseNumber = getTextFromElement(courseNumberElement);
				int courseCredit = Integer.parseInt(getTextFromElement(courseCreditElement));
				findAndSaveCourse(courseNumber, courseCredit, courseDtoList);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void findAndSaveCourse(String courseNumber, int courseCredit, List<CourseDto> courseDtoList) {
		if (courseNumber != null) {
			CourseDto courseDto = courseRepository.findCourseDtoByNumberAndCredit(courseNumber, courseCredit);
			if (courseDto == null) return;
			courseDtoList.add(courseDto);
		}
	}

	private String getTextFromElement(Element element) {
		if(element != null) {
			return element.text();
		}
		return "";
	}

	private Boolean isRetakenCourse(Element tableRow) {
		Elements children = tableRow.children();
		if(children.last() == null || children.last().previousElementSibling() == null) return false;
		return children.last().text().equals("재수강") || children.last().previousElementSibling().text().equals("F");
	}

	private Boolean isCourseInfoRow(Element tableRow) {
		return tableRow != null && tableRow.childrenSize() >= 4;
	}

	private Elements getValidTableElements(Element body) throws InvalidDocumentException {
		validateBodyElement(body);
		Elements bodyChildren = body.children();
		filterTableChildren(bodyChildren);
		return bodyChildren;
	}

	private void filterTableChildren(Elements elements) {
		elements.removeIf(element -> !hasElementChildTable(element));
	}

	private void validateBodyElement(Element body) throws InvalidDocumentException {
		if (body == null) {
			throw new InvalidDocumentException("과목 페이지에서 body를 찾을 수 없습니다.");
		}
		Elements bodyChildren = body.children();
		Integer bodyChildrenSize = body.childrenSize();
		if (bodyChildren == null || bodyChildrenSize == null || bodyChildrenSize <= 2) {
			throw new InvalidDocumentException("신입생이거나, 조회되는 수강 과목이 없습니다.");
		}
	}

	private Boolean hasElementChildTable(Element element) {
		return element.children() != null
				&& element.childrenSize() >= 1
				&& element.children().first().is("table");
	}

	public List<CourseDto> getUserTakenCoursesFromClassnet(HttpServletRequest request) throws InvalidCookieException, InvalidDocumentException {
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

	public Integer getUserTakenTotalCredit(List<CourseDto> userTakenCourses) {
		Integer ret = 0;
		for (CourseDto courseDto : userTakenCourses) {
			ret += courseDto.getCredit();
		}
		return ret;
	}

//	private List<CourseDto> getMajorCourseList(Major major, int enterYear) {
//		List<CourseDto> majorCourseList = majorHierarchyRepository.findAllMajorCoursesByMaster(major);
//		String majorCode = major.getCode();
//		String college = major.getCollege();
//		if (college.equals("미술대학")) {
//			majorCourseList.removeIf(courseDto -> courseDto.getNumber().equals("400101")
//					|| courseDto.getNumber().equals("400102")
//					|| courseDto.getNumber().equals("400201")
//					|| courseDto.getNumber().equals("400202")
//			);
//		}
//		if (majorCode.equals("CS")) {
//			return getCSMajorCourseList(enterYear);
//		}
//		return majorCourseList;
//	}
//
//	private List<CourseDto> getCSMajorCourseList(int enterYear) {
//		List<CourseDto> majorCourseList = majorHierarchyRepository.findAllMajorCoursesByMaster("CS");
//		if (enterYear >= 20) {
//			majorCourseList.removeIf(courseDto -> {
//				String courseNumber = courseDto.getNumber();
//				return courseNumber.equals("004174")
//						|| courseNumber.equals("101810")
//						|| courseNumber.equals("012305");
//			});
//		}
//		return majorCourseList;
//	}
}
