package com.hongikgrad.course.application;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.crawler.CourseCrawler;
import com.hongikgrad.common.crawler.UserCourseCrawler;
import com.hongikgrad.course.dto.CourseResponseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.UserCourse;
import com.hongikgrad.course.repository.CourseRepository;
import com.hongikgrad.course.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final UserCourseCrawler userCourseCrawler;
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseCrawler courseCrawler;

    public List<CourseResponseDto> getUserTakenCourses(HttpServletRequest request) throws IOException, NullPointerException{
        String studentId = getStudentIdFromCookie(request);
        return userCourseRepository.findUserTakenCoursesByStudentId(studentId);
    }

    /* 유저가 들은 과목들 클래스넷에서 가져와서 저장 */
    public void saveUserTakenCourses(HttpServletRequest request) throws IOException, NullPointerException {
        List<CourseResponseDto> userTakenCourses = userCourseCrawler.getUserTakenCoursesFromClassnet(request);
        User user = userRepository.findByStudentId(getStudentIdFromCookie(request));
        for (CourseResponseDto userTakenCourse : userTakenCourses) {
            Course course = courseRepository.findByNumberAndAndCredit(userTakenCourse.getNumber(), userTakenCourse.getCredit());
            UserCourse userCourse = new UserCourse(user, course);
            userCourseRepository.save(userCourse);
        }
    }

    /* 홍익대 시간표에서 과목을 가져와서 저장 */
    public void saveAbeekCoursesFromTimeTable(Map<String, String> data) throws IOException, IndexOutOfBoundsException {
        List<Course> courses = courseCrawler.getAbeekCoursesFromTimeTable(data);
        for (Course course : courses) {
            if (courseRepository.existsCourseByNumberAndCredit(course.getNumber(), course.getCredit())) continue;
            courseRepository.save(course);
        }
    }

    public void saveNonAbeekCoursesFromTimeTable(Map<String, String> data) throws IOException, IndexOutOfBoundsException {
        List<Course> courses = courseCrawler.getNonAbeekCoursesFromTimeTable(data);
        for (Course course : courses) {
            if (courseRepository.existsCourseByNumberAndCredit(course.getNumber(), course.getCredit())) continue;
            courseRepository.save(course);
        }
    }

    private String getStudentIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String key = cookie.getName();
            if (key.equals("sid")) {
                return cookie.getValue();
            }
        }
        throw new NullPointerException();
    }

}
