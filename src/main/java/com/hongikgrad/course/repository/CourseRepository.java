package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
    public Course findByNumberAndAndCredit(String number, int credit);
    public Boolean existsCourseByNumberAndCredit(String number, int credit);
}
