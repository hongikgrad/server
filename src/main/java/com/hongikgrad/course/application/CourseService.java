package com.hongikgrad.course.application;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.application.CookieService;
import com.hongikgrad.common.crawler.CourseCrawler;
import com.hongikgrad.common.crawler.UserCourseCrawler;
import com.hongikgrad.course.dto.CourseCrawlingDto;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.dto.CourseResponseDto;
import com.hongikgrad.course.dto.UserTakenCourseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.UserCourse;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.course.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseService {

    /* 이렇게 많아도 되나...?? */
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private final UserCourseCrawler userCourseCrawler;
    private final CourseCrawler courseCrawler;

    private final CookieService cookieService;

    public List<CourseResponseDto> getAllCourses() {
        List<CourseResponseDto> ret = new ArrayList<>();
        List<Course> courses = courseRepository.findAll();
        courses.forEach((course) -> {
            ret.add(new CourseResponseDto(course.getName(), course.getCredit(), course.getNumber()));
        });
        return ret;
    }

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

    /* 홍익대 시간표 사이트에서 과목들을 크롤링한 뒤 저장 */
    public void saveCoursesFromTimeTable(Map<String, String> data) throws IOException {
        try {
            List<Course> courses = new ArrayList<>();
            Set<CourseCrawlingDto> courseDtos = courseCrawler.getCoursesFromTimeTable(data);
            courseDtos.forEach((course) -> {
                if (!courseRepository.existsCourseByNumberAndCredit(course.getNumber(), course.getCredit())) {
                    courses.add(new Course(course.getName(), course.getCredit(), course.getNumber(), course.getAbeek()));
                }
            });
            System.out.println(data.get("p_yy") + data.get("p_hakgi") + data.get("p_dept"));
            courseRepository.saveAll(courses);
        } catch(IndexOutOfBoundsException ignored) {}
    }
}
