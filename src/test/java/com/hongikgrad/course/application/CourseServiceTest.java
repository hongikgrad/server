package com.hongikgrad.course.application;

import com.hongikgrad.course.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    public void test() {
        long count = courseService.getAllCoursesCount();
        System.out.println("count = " + count);
    }

}