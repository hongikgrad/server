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

    /* 유저가 들은 과목들 클래스넷에서 가져와서 저장 */
    public void saveUserTakenCourses(HttpServletRequest request) throws IOException, NullPointerException {
        List<CourseResponseDto> userTakenCourses = userCourseCrawler.getUserTakenCoursesFromClassnet(request);
        User user = userRepository.findByStudentId(cookieService.getStudentIdFromCookie(request));
        for (CourseResponseDto userTakenCourse : userTakenCourses) {
            Course course = courseRepository.findByNumberAndAndCredit(userTakenCourse.getNumber(), userTakenCourse.getCredit());
            if(course == null) {
                System.out.println("userTakenCourse = " + userTakenCourse.getName() + " " + userTakenCourse.getNumber());
            } else if (!userCourseRepository.existsUserTakenCourse(user, course)) {
                userCourseRepository.save(new UserCourse(user, course));
            }
        }
    }

    public void saveMajorCourses(Set<MajorCourse> majorCourses) {
        majorCourseRepository.saveAll(majorCourses);
    }

    /* 홍익대 시간표 사이트에서 과목들을 크롤링해서 가져옴 */
    public MajorCourseListDto getCoursesFromTimeTable(Map<String, String> data) throws IOException {
        try {
            CrawlingCourseListDto result = courseCrawler.getCoursesFromTimeTable(data);

            Set<Major> majors = new HashSet<>();
            Set<String> majorNames = result.getMajors();
            for (String majorName : majorNames) {
                Optional<Major> optionalMajor = majors.stream().filter(m -> m.getName().equals(majorName)).findFirst();
                Major major;
                if(optionalMajor.isPresent()) {
                    major = optionalMajor.get();
                } else {
                    major = majorRepository.findMajorByName(majorName);
                    if(major == null) {
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
                if(course == null) {
                    course = courseRepository.save(new Course(courseDto.getName(), courseDto.getCredit(), courseDto.getNumber(), courseDto.getAbeek()));
                }
                if(courseDto.getAbeek().contains("전") && !courseDto.getAbeek().contains("MSC")) {
                    Major major = majors.stream().filter(m -> m.getName().equals(courseDto.getMadeBy())).findFirst().get();
                    MajorCourse majorCourse = new MajorCourse(major, course, courseDto.getAbeek().equals("전필"));
                    majorCourses.add(majorCourse);
                }
            }
            return new MajorCourseListDto(majorCourses, courses);
        } catch(IndexOutOfBoundsException ignored) {
            return null;
        }
    }
}
