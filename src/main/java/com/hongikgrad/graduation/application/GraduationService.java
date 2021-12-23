package com.hongikgrad.graduation.application;

import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.dto.DragonballDto;
import com.hongikgrad.course.entity.Major;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.course.repository.MajorCourseRepository;
import com.hongikgrad.course.repository.MajorRepository;
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

	private final UserCourseRepository userCourseRepository;
	private final CourseRepository courseRepository;
	private final MajorCourseRepository majorCourseRepository;
	private final MajorRepository majorRepository;

	private final CookieService cookieService;

	public List<RequiredCoursesDto> getRequirementsForGraduation(HttpServletRequest request) {
		List<RequiredCoursesDto> totalRequiredCourses = new ArrayList<>();
		String studentId = cookieService.getStudentIdFromCookie(request);
		int studentEnter = Integer.parseInt(cookieService.getStudentEnterFromCookie(request));
		Major studentMajor = majorRepository.findMajorByCode(cookieService.getStudentMajorFromCookie(request));
		List<CourseDto> userTakenCourses = userCourseRepository.findUserTakenCoursesByStudentId(studentId);

		List<CourseDto> userTakenAbeekCourses = userTakenCourses.stream()
				.filter((course) -> course.getAbeek().length() >= 3 && !course.getAbeek().contains("MSC"))
				.collect(Collectors.toList());

		List<CourseDto> userTakenMSCCourses = userTakenCourses.stream()
				.filter(course -> course.getAbeek().contains("MSC"))
				.collect(Collectors.toList());

		List<CourseDto> userTakenMajorCourses = userCourseRepository.findUserTakenMajorCoursesByStudentId(studentId, studentMajor);

		/* abeek(영어, 글쓰기, 드래곤볼) 검사 */
		Map<String, Object> requiredAbeekCourses = getRequiredAbeekCourses(userTakenAbeekCourses);
		totalRequiredCourses.add(new RequiredCoursesDto("교양", requiredAbeekCourses));

		/* MSC 검사 */
		Map<String, Integer> mscRequiredCredits = getNotTakenMSCAreas(userTakenMSCCourses);
		totalRequiredCourses.add(new RequiredCoursesDto("requiredMSCCredits", mscRequiredCredits));

		List<CourseDto> mscRequiredCourses = getNotTakenRequiredMSCCourses(userTakenMSCCourses, studentMajor, studentEnter);
		totalRequiredCourses.add(new RequiredCoursesDto("requiredMSCCourses", mscRequiredCourses));

		/* 필수 전공 검사 */
		List<CourseDto> majorRequirements = getNotTakenRequiredMajors(userTakenMajorCourses, studentMajor, studentEnter);
		totalRequiredCourses.add(new RequiredCoursesDto("requiredMajorCourses", majorRequirements));

		/* 전공 학점 검사 */
		int totalMajorCredits = getUserTakenTotalMajorCredits(userTakenMajorCourses);
		totalRequiredCourses.add(new RequiredCoursesDto("totalMajorCredits", totalMajorCredits));

		int requiredMajorCredits = Math.max(50 - totalMajorCredits, 0);
		totalRequiredCourses.add(new RequiredCoursesDto("requiredMajorCredits", requiredMajorCredits));

		/* 전체 학점 검사 */
		int totalCredits = getUserTakenTotalCredits(userTakenCourses);
		totalRequiredCourses.add(new RequiredCoursesDto("totalCredits", totalCredits));

		int requiredTotalCredits = Math.max(132 - totalCredits, 0);
		totalRequiredCourses.add(new RequiredCoursesDto("requiredTotalCredits", requiredTotalCredits));

		return totalRequiredCourses;
	}

	private Map<String, Object> getRequiredAbeekCourses(List<CourseDto> courses) {
		Map<String, Object> requirements = new HashMap<>();
		List<String> requiredAreas = new ArrayList<>(List.of(
				"역사와문화",
				"언어와철학",
				"사회와경제",
				"법과생활",
				"공학의이해",
				"제2외국어와한문",
				"예술과디자인"
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
		List<String> majorEnglishCourseNumbers = new ArrayList<>(List.of(
				"007114",
				"007115"
		));

		boolean isEnglishTaken = false;
		boolean isMajorEnglishTaken = false;
		boolean isWritingTaken = false;

		for (CourseDto course : courses) {
			String courseNumber = course.getNumber();
			String courseAbeek = course.getAbeek();
			boolean removed = requiredAreas.removeIf(area -> area.equals(courseAbeek));
			if (!removed && writingCourseNumbers.contains(courseNumber)) {
				isWritingTaken = true;
			} else if(!isMajorEnglishTaken && majorEnglishCourseNumbers.contains(courseNumber)) {
				isMajorEnglishTaken = true;
			} else if(!isEnglishTaken && englishCourseNumber.equals(courseNumber)) {
				isEnglishTaken = true;
			}
		}

		/**
		 * 1. 예술들음, 나머지 5개들음 -> 역사 들어야됨
		 * 2. 에술들음 나머지 4개들음. 제2외국어, 역사 남음 -> 제2외국어만 들으면됨
		 * 3. 둘다안들음, 나머지 5개들음 -> 외궈 예술 둘다 들어야됨
		 */

		List<DragonballDto> dragonballs = new ArrayList<>();

		int currentSize = 7 - requiredAreas.size();

		if(requiredAreas.contains("제2외국어와한문")) {
			List<CourseDto> coursesByAbeek = courseRepository.findCoursesByAbeek("제2외국어와한문");
			dragonballs.add(new DragonballDto("제2외국어와한문", true, coursesByAbeek));
			currentSize += 1;
			requiredAreas.remove("제2외국어와한문");
		}

		if(requiredAreas.contains("예술과디자인")) {
			List<CourseDto> coursesByAbeek = courseRepository.findCoursesByAbeek("예술과디자인");
			dragonballs.add(new DragonballDto("예술과디자인", true, coursesByAbeek));
			currentSize += 1;
			requiredAreas.remove("예술과디자인");
		}

		if(currentSize < 6) {
			for(String area : requiredAreas) {
				List<CourseDto> coursesByAbeek = courseRepository.findCoursesByAbeek(area);
				dragonballs.add(new DragonballDto(area, false, coursesByAbeek));
			}
		}

		requirements.put("드래곤볼", dragonballs);

		if(!isWritingTaken) {
			List<CourseDto> writingCourses = courseRepository.findWritingCourses();
			requirements.put("글쓰기", writingCourses);
		}

		if(!isMajorEnglishTaken) {
			List<CourseDto> majorEnglishCourses = courseRepository.findMajorEnglishCourses();
			requirements.put("전공기초영어", majorEnglishCourses);
		}

		if(!isEnglishTaken) {
			List<CourseDto> englishCourse = courseRepository.findEnglishCourse();
			requirements.put("영어", englishCourse);
		}

		return requirements;
	}

	private Map<String, Integer> getNotTakenMSCAreas(List<CourseDto> courses) {
		Map<String, Integer> requirements = new HashMap<String, Integer>(Map.of(
				"MSC수학", 9,
				"MSC과학", 9,
				"MSC전산", 6
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

	private List<CourseDto> getNotTakenRequiredMSCCourses(List<CourseDto> courses, Major studentMajor, int studentEnter) {
		List<CourseDto> requiredCourses = majorCourseRepository.findRequiredMSCCourse();
		String majorName = studentMajor.getName();
		//			requiredCourses.add("012104"); //대학물리2 *
//			requiredCourses.add("012106"); //대학물리2실험 *
//
//			requiredCourses.add("012107"); //대학화학1 *
//			requiredCourses.add("012109"); //대학화학1실험 *
//
//			requiredCourses.add("012110"); //대학화학2
//			requiredCourses.add("012113"); //대학화학2실험
//
//			requiredCourses.add("012101"); //대학물리1
//			requiredCourses.add("012103"); //대학물리1실험

		if(majorName.contains("전자")) {
			// 16-22학번
			for(CourseDto courseDto : courses) {
				String courseNumber = courseDto.getNumber();
				requiredCourses.removeIf(course -> course.getNumber().equals(courseNumber));
			}
			if(!requiredCourses.contains(new CourseDto("012110", 3))
					&& !requiredCourses.contains(new CourseDto("012113", 1))) {
				requiredCourses.removeIf(course -> course.getNumber().equals("012101") || course.getNumber().equals("012103"));
			} else if (!requiredCourses.contains(new CourseDto("012101", 3))
					&& !requiredCourses.contains(new CourseDto("012103", 1))) {
				requiredCourses.removeIf(course -> course.getNumber().equals("012110") || course.getNumber().equals("012113"));
			}
		} else if(majorName.contains("컴퓨터")) {
			// 컴공 비공학

		} else if(majorName.contains("기계")) {
			// 기계 공학

		} else if(majorName.contains("화학")) {
			// 화공 비공학

		} else if(majorName.contains("신소재")) {
			// 신소재 비공학

		} else if(majorName.contains("산업공")) {
			// 산공 공학

		}

		return requiredCourses;
	}

	private List<CourseDto> getNotTakenRequiredMajors(List<CourseDto> courses, Major studentMajor, int studentEnter) {
		List<CourseDto> requiredCourses = majorCourseRepository.findRequiredCoursesByMajor(studentMajor);
		for(CourseDto courseDto : courses) {
			requiredCourses.removeIf(course -> course.getName().equals(courseDto.getName()));
		}

		if(studentMajor.getName().contains("전자")) {
			if (!requiredCourses.contains(new CourseDto("013704", 2))) {
				requiredCourses.remove(new CourseDto("106824", 3));
			}

			if (!requiredCourses.contains(new CourseDto("013805", 3))) {
				requiredCourses.remove(new CourseDto("106825", 3));
			}

			if (!requiredCourses.contains(new CourseDto("106820", 3))) {
				requiredCourses.remove(new CourseDto("106827", 3));
			}

			requiredCourses.remove(new CourseDto("013704", 2));
			requiredCourses.remove(new CourseDto("013805", 3));
			requiredCourses.remove(new CourseDto("106820", 3));

			if(studentEnter <= 16) {
				requiredCourses.remove(new CourseDto("106827", 3));
			}

			return requiredCourses;
		} else if(studentMajor.getName().contains("컴퓨터")) {
			/* 자료구조 삭제 */
			if (!requiredCourses.contains(new CourseDto("013312", 3))) {
				requiredCourses.remove(new CourseDto("013312", 4));
			} else if (!requiredCourses.contains(new CourseDto("013312", 4))) {
				requiredCourses.remove(new CourseDto("013312", 3));
			}
		}
		return null;
	}

	private int getUserTakenTotalMajorCredits(List<CourseDto> courses) {
		int totalCredits = 0;
		for(CourseDto course : courses) {
			totalCredits += course.getCredit();
		}
		return totalCredits;
	}

	private int getUserTakenTotalCredits(List<CourseDto> courses) {
		int requiredCredits = 0;
		for(CourseDto courseDto : courses) {
			requiredCredits += courseDto.getCredit();
		}
		return requiredCredits;
	}
}
