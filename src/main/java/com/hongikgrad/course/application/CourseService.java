package com.hongikgrad.graduation.application;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.common.crawler.UserCourseCrawler;
import com.hongikgrad.graduation.dto.CourseResponseDto;
import com.hongikgrad.graduation.entity.Course;
import com.hongikgrad.graduation.entity.UserCourse;
import com.hongikgrad.graduation.repository.CourseRepository;
import com.hongikgrad.graduation.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final UserCourseCrawler userCourseCrawler;
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<CourseResponseDto> getUserTakenCourses(HttpServletRequest request) throws IOException {
        return userCourseCrawler.getUserTakenCoursesFromClassnet(request);
    }

    public void saveUserTakenCourses(String studentId, List<CourseResponseDto> userTakenCourses) {
        User user = userRepository.findByStudentId(studentId);
        for (CourseResponseDto userTakenCourse : userTakenCourses) {
            Course course = courseRepository.findByNameAndAndCredit(userTakenCourse.getName(), userTakenCourse.getCredit());
            UserCourse userCourse = new UserCourse(user, course);
            userCourseRepository.save(userCourse);
        }
    }

}
