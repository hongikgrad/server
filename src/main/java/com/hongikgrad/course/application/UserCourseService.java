package com.hongikgrad.course.application;

import com.hongikgrad.common.crawler.CourseCrawler;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.exception.InvalidCookieException;
import com.hongikgrad.course.exception.InvalidDocumentException;
import com.hongikgrad.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserCourseService {

	private final CourseCrawler courseCrawler;
	private final CourseRepository courseRepository;

	String TAKEN_COURSE_URL = "https://cn.hongik.ac.kr/stud/P/01000/01000.jsp";
	String GRADUATION_URL = "https://cn.hongik.ac.kr/stud/E/04000/04010.jsp";

	public List<CourseDto> loadUserTakenCourses(Map<String, String> request) throws InvalidCookieException, InvalidDocumentException {
		return getUserTakenCoursesFromClassnet(request);
	}

	public List<CourseDto> loadUserTakenCoursesV2(Map<String, String> request) throws InvalidDocumentException {
		return getUserTakenCourseFromGraduationPage(request);
	}

	public List<CourseDto> getUserTakenCourseFromGraduationPage(Map<String, String> request) throws InvalidDocumentException {
		List<CourseDto> courseDtoList = new ArrayList<>();
		Map<String, String> data = Map.of(
				"gubun", "1",
				"dept", "A000"
		);
		Document userTakenCourseDocument = courseCrawler.getJsoupResponseDocument(GRADUATION_URL, request, courseCrawler.getHeaders("https://cn.hongik.ac.kr/stud/E/04000/04000.jsp"), data, Connection.Method.POST);
		try {
			Element body = userTakenCourseDocument.getElementById("body");
			Elements tableWrappers = body.child(2).children();
			for (Element tableWrapper : tableWrappers) {
				Elements tableElements = tableWrapper.children();
				for (Element tableElement : tableElements) {
					Element tableBody = tableElement.selectFirst("tbody");
					if (tableBody == null) continue;
					Elements tableRows = tableBody.children();
					extractCourseData(tableRows, courseDtoList, 0, 3);
				}
			}
			return courseDtoList;
		} catch(Exception e) {
			log.error(e.toString());
			return null;
		}
	}

	public List<CourseDto> getUserTakenCoursesFromClassnet(Map<String, String> request) throws InvalidDocumentException, InvalidCookieException {
		List<CourseDto> courseDtoList = new ArrayList<>();
		Document userTakenCourseDocument = courseCrawler.getJsoupResponseDocument(TAKEN_COURSE_URL, request, courseCrawler.getHeaders(), null, Connection.Method.POST);
		try {
			Element body = userTakenCourseDocument.getElementById("body");
			Elements semesterTableList = getValidTableElements(body);

			for (Element table : semesterTableList) {
				Element tableHead = table.selectFirst("thead");
				int courseNumberIndex = getCourseNumberIndex(tableHead);
				int courseCreditIndex = getCourseCreditIndex(tableHead);

				Element tableBody = table.selectFirst("tbody");
				if (tableBody == null) {
					continue;
				}
				Elements tableRows = tableBody.children();
				extractCourseData(tableRows, courseDtoList, courseNumberIndex, courseCreditIndex);
			}
			return courseDtoList;
		} catch(Exception e) {
			log.error(e.toString());
			return null;
		}
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
		if (element != null) {
			return element.text();
		}
		return "";
	}

	private Boolean isRetakenCourse(Element tableRow) {
		Elements children = tableRow.children();
		if (children.last() == null || children.last().previousElementSibling() == null) return false;
		return children.last().text().equals("재수강") || children.last().previousElementSibling().text().equals("F");
	}

	private Boolean isCourseInfoRow(Element tableRow) {
		return tableRow != null && tableRow.childrenSize() >= 4;
	}

	private int getCourseCreditIndex(Element tableHead) {
		return getColumnIndex(tableHead, "학점");
	}

	private int getCourseNumberIndex(Element tableHead) {
		return getColumnIndex(tableHead, "학수번호");
	}

	private Elements getValidTableElements(Element body) throws InvalidDocumentException {
		validateBodyElement(body);
		Elements bodyChildren = body.children();
		filterTableChildren(bodyChildren);
		return bodyChildren;
	}

	private int getColumnIndex(Element tableHead, String columnText) {
		if (tableHead == null) return -1;
		Element row = tableHead.child(0);
		for (int index = 0; index < row.childrenSize(); index++) {
			String text = row.child(index).text();
			if (text.equals(columnText)) {
				return index;
			}
		}
		return 0;
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

	private void filterTableChildren(Elements elements) {
		elements.removeIf(element -> !hasElementChildTable(element));
	}

	private Boolean hasElementChildTable(Element element) {
		return element.children() != null
				&& element.childrenSize() >= 1
				&& element.children().first().is("table");
	}

	public Integer getUserTakenTotalCredit(List<CourseDto> userTakenCourses) {
		Integer ret = 0;
		for (CourseDto courseDto : userTakenCourses) {
			ret += courseDto.getCredit();
		}
		return ret;
	}
}
