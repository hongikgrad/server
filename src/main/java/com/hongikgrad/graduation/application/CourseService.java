package com.hongikgrad.graduation.application;

import com.hongikgrad.graduation.dto.CourseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseCrawler courseCrawler;

    public List<CourseResponseDto> getUserTakenCourses(HttpServletRequest request) throws IOException {
        return courseCrawler.getUserTakenCoursesFromClassnet(request);
    }

}
