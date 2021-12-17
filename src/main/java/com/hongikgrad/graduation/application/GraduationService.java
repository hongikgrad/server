package com.hongikgrad.graduation.application;

import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.dto.CourseResponseDto;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.course.repository.UserCourseRepository;
import com.hongikgrad.graduation.dto.RequiredCoursesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraduationService {

	UserCourseRepository userCourseRepository;
	CourseRepository courseRepository;
	CookieService cookieService;

	public void getUserMajor() {
	}

	public List<RequiredCoursesDto> getRequiredCourses(HttpServletRequest request) {
		String studentId = cookieService.getStudentIdFromCookie(request);
		List<CourseDto> userTakenCourses = userCourseRepository.findUserTakenCoursesByStudentId(studentId);

		List<CourseDto> abeek = userTakenCourses.stream()
				.filter((course) -> course.getAbeek().length() >= 3 && !course.getAbeek().contains("MSC"))
				.collect(Collectors.toList());

		List<CourseDto> msc = userTakenCourses.stream()
				.filter(course -> course.getAbeek().contains("MSC"))
				.collect(Collectors.toList());

		List<CourseDto> major = userTakenCourses.stream()
				.filter(course -> course.getAbeek().contains("전") && course.getAbeek().length() >= 3)
				.collect(Collectors.toList());

		userTakenCourses.removeIf(courseDto ->
				(courseDto.getAbeek().length() >= 3 || (courseDto.getAbeek().contains("전") && courseDto.getAbeek().length() <= 2))
		);

		List<RequiredCoursesDto> totalRequiredCourses = new ArrayList<>();

		/* abeek(영어, 글쓰기, 드래곤볼) 검사 */
		List<String> abeekRequirements = getNotTakenAbeekAreas(abeek);
		for (String area : abeekRequirements) {
			List<CourseDto> courses = getCoursesMatchedArea(area);
			totalRequiredCourses.add(new RequiredCoursesDto(area, courses));
		}

		/* MSC 검사 */
		Map<String, Integer> mscRequirements = getNotTakenMSCAreas(msc);

		/* 전공필수, 전공 총 학점 검사 */
		Map<String, Integer> majorRequirements = getNotTakenRequiredMajors(major);

		/* 전체학점 검사 */

		List<String> notTakenMSCAreas = null;
		// 졸업요건 만족 검사
		return totalRequiredCourses;
	}

	private List<String> getNotTakenAbeekAreas(List<CourseDto> courses) {
		// TODO: 과목 체크하고 조건 전부 맞으면 null 보내기
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
			boolean isDragonball = requiredCourses.removeIf(area -> area.equals(courseAbeek));
			if (!isDragonball) {
				if (requiredCourses.contains("영어") && courseNumber.equals(englishCourseNumber)) {
					requiredCourses.remove("영어");
				}
				if (requiredCourses.contains("글쓰기")) {
					boolean isRemoved = false;
					for (String writingCourseNumber : writingCourseNumbers) {
						if (!isRemoved && writingCourseNumber.equals(courseNumber)) {
							requiredCourses.remove("글쓰기");
							isRemoved = true;
						}
					}
				}
				if (requiredCourses.contains("전공기초영어")) {
					boolean isRemoved = false;
					for (String englishForMajorCourseNumber : englishForMajorCourseNumbers) {
						if (!isRemoved && englishForMajorCourseNumber.equals(courseNumber)) {
							requiredCourses.remove("전공기초영어");
							isRemoved = true;
						}
					}
				}
			}
		});

		return requiredCourses;
	}

	private Map<String, Integer> getNotTakenMSCAreas(List<CourseDto> courses) {
		/* 전공별 상이 */
		Map<String, Integer> requirements = new HashMap<String, Integer>(Map.of(
				"MSC수학", 9,
				"MSC과학", 9,
				"MSC전산", 6
		));

		/* 전공별 상이 */
		List<String> requiredCourses = new ArrayList<>(List.of(
				"대학물리실험1",
				"대학물리실험2"
		));

		courses.forEach(course -> {
			String area = course.getAbeek();
			Integer credit = requirements.get(course.getAbeek()) - course.getCredit();
			requirements.put(area, credit);
		});

		if (requirements.get("MSC수학") <= 0) {
			requirements.remove("MSC수학");
		}

		if (requirements.get("MSC과학") <= 0) {
			requirements.remove("MSC과학");
		}

		if (requirements.get("MSC전산") <= 0) {
			requirements.remove("MSC전산");
		}

		return requirements;
	}

	private Map<String, Integer> getNotTakenRequiredMajors(List<CourseDto> courses) {
		List<String> requiredMajor = new ArrayList<>(List.of(
				"111",
				"222",
				"333"
		));
		// TODO : 전공에 따른 전공필수 테이블에 저장된거 불러오기
		return null;
	}

	private List<CourseDto> getCoursesMatchedArea(String area) {
		return null;
	}
}
