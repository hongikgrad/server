package com.hongikgrad.course.application;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.common.crawler.CourseCrawler;
import com.hongikgrad.common.crawler.UserCourseCrawler;
import com.hongikgrad.course.dto.*;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.Major;
import com.hongikgrad.course.entity.MajorCourse;
import com.hongikgrad.course.entity.UserCourse;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.course.repository.MajorCourseRepository;
import com.hongikgrad.course.repository.MajorRepository;
import com.hongikgrad.course.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

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

    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses() {
        List<CourseResponseDto> ret = new ArrayList<>();
        List<Course> courses = courseRepository.findAll();
        courses.forEach((course) -> {
            ret.add(new CourseResponseDto(course.getName(), course.getCredit(), course.getNumber()));
        });
        return ret;
    }

    @Transactional(readOnly = true)
    public UserTakenCourseDto getUserTakenCourses(HttpServletRequest request) throws IOException, NullPointerException{
        String studentId = cookieService.getStudentIdFromCookie(request);
        List<CourseDto> userTakenCourses = userCourseRepository.findUserTakenCoursesByStudentId(studentId);
        int totalCredit = 0;
        int totalCount = userTakenCourses.size();
        for (CourseDto course : userTakenCourses) {
            totalCredit += course.getCredit();
        }
        return new UserTakenCourseDto(totalCredit, totalCount, userTakenCourses);
    }

    @Transactional(readOnly = true)
    public int getUserTakenTotalCredit(HttpServletRequest request) {
        String studentId = cookieService.getStudentIdFromCookie(request);
        return 0;
    }

    /* 유저가 들은 과목들 클래스넷에서 가져와서 저장 */
    public void saveUserTakenCourses(HttpServletRequest request) throws IOException, NullPointerException {
        List<CourseResponseDto> userTakenCourses = userCourseCrawler.getUserTakenCoursesFromClassnet(request);
        User user = userRepository.findByStudentId(cookieService.getStudentIdFromCookie(request));
        for (CourseResponseDto userTakenCourse : userTakenCourses) {
            Course course = courseRepository.findByNumberAndAndCredit(userTakenCourse.getNumber(), userTakenCourse.getCredit());
            UserCourse userCourse = new UserCourse(user, course);
            userCourseRepository.save(userCourse);
        }
    }

    public void saveCourses(Set<Course> courses) {
//        courseRepository.saveAll(courses);
    }

    public void saveMajorCourses(Set<MajorCourse> majorCourses) {
//        majorCourseRepository.saveAll(majorCourses);
    }

    /* 홍익대 시간표 사이트에서 과목들을 크롤링해서 가져옴 */
    public CrawlingCourseListDto getCoursesFromTimeTable(Map<String, String> data) throws IOException {
        try {
            Set<Course> courses = new HashSet<>();
            Set<MajorCourse> majorCourses = new HashSet<>();
            Set<CrawlingCourseDto> courseDtos = courseCrawler.getCoursesFromTimeTable(data);
            Major major = null;
            for (CrawlingCourseDto courseDto : courseDtos) {
                if (major == null || !major.getName().equals(courseDto.getMadeBy())) {
                    major = majorRepository.findMajorByName(courseDto.getMadeBy());
                }
                Course course = null;
                if(!courseRepository.existsCourseByNumberAndCredit(courseDto.getNumber(), courseDto.getCredit())) {
                    course = courseRepository.save(new Course(courseDto.getName(), courseDto.getCredit(), courseDto.getNumber(), courseDto.getAbeek()));
                } else {
                    course = courseRepository.findByNumberAndAndCredit(courseDto.getNumber(), courseDto.getCredit());
                }
                if (!courseDto.getMadeBy().contains("교양")) {
                    if (major == null) major = majorRepository.saveAndFlush(new Major(courseDto.getMadeBy()));
                    if (course.getAbeek().equals("전필")) {
                        majorCourses.add(new MajorCourse(major, course, true));
                    } else {
                        majorCourses.add(new MajorCourse(major, course, false));
                    }
                }
            }
            majorCourseRepository.saveAllAndFlush(majorCourses);
            return new CrawlingCourseListDto(courses, majorCourses);
        } catch(IndexOutOfBoundsException ignored) {
            return null;
        }
    }
}
