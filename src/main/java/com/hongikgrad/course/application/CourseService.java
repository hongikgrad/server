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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    private List<CourseDto> allCourses;

    @PostConstruct
    public void init() {
        allCourses = courseRepository.findAllCourseDto();
    }

    public List<CourseDto> getAllCourses(Pageable pageable) {
        int fromIndex = Optional.of(pageable.getOffset()).orElse(0L).intValue();
        int toIndex = Optional.of(pageable.getOffset() + pageable.getPageSize()).orElse(0L).intValue();
        return allCourses.subList(fromIndex, toIndex);
    }

    public List<CourseDto> searchCourses(String keyword) {
        return allCourses.stream().filter((course) -> course.getName().contains(keyword)).collect(Collectors.toList());
    }

    public List<CourseDto> searchCoursesWithCommand(String keyword, String command) {
        if(command.equals("cat")) {
            if(keyword.equals("글쓰기")) {
                return courseRepository.findWritingCourses();
            } else if(keyword.equals("전공기초영어")) {
                return courseRepository.findMajorEnglishCourses();
            } else if(keyword.equals("영어")) {
                return courseRepository.findEnglishCourse();
            }
            return allCourses.stream().filter(course -> course.getAbeek().contains(keyword)).collect((Collectors.toList()));
        } else if(command.equals("major")) {
            Major major = majorRepository.findMajorByNameContains(keyword);
            return majorCourseRepository.findCoursesByMajor(major);
        } else return null;
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getAllMajorCourses(String majorCode) {
        Major studentMajor = majorRepository.findMajorByCode(majorCode.toUpperCase(Locale.ROOT));
        return majorCourseRepository.findCoursesByMajor(studentMajor);
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* 홍익대 시간표 사이트에서 과목들을 크롤링해서 가져옴 */
    public void getCoursesFromTimeTable(String semester, Map<String, String> data) throws IOException {
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
                    course = courseRepository.save(new Course(courseDto.getName(), courseDto.getCredit(), courseDto.getNumber(), courseDto.getAbeek(), semester));
                } else {
                    // 이미 존재하는 수업 학기 갱신
                    int newSemester = Integer.parseInt(semester);
                    int oldSemester = 0;
                    if(course.getSemester() != null) {
                        oldSemester = Integer.parseInt(course.getSemester());
                    }
                    if (newSemester > oldSemester) {
                        course.changeSemester(semester);
                    }
                }
                // 전공여부 확인
                if(courseDto.getAbeek().contains("전") && !courseDto.getAbeek().contains("MSC")) {
                    Major major = majors.stream().filter(m -> m.getName().equals(courseDto.getMadeBy())).findFirst().get();
                    MajorCourse findMajorCourse = majorCourseRepository.findMajorCourseByCourseAndMajor(course, major);
                    if(findMajorCourse == null) {
                        MajorCourse majorCourse = new MajorCourse(major, course, courseDto.getAbeek().equals("전필"));
                        majorCourseRepository.save(majorCourse);
                    }
//                    majorCourses.add(majorCourse);
                }
            }
//            return new MajorCourseListDto(majorCourses, courses);
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
//            return null;
        }
    }

    public long getAllCoursesCount() {
        return courseRepository.count();
    }
}
