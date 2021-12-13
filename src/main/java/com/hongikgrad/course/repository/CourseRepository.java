package com.hongikgrad.course.repository;

import com.hongikgrad.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    public Course findByNumberAndAndCredit(String number, int credit);
    public Boolean existsCourseByNumberAndCredit(String number, int credit);
}
