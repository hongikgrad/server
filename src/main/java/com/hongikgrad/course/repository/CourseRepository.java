package com.hongikgrad.graduation.repository;

import com.hongikgrad.graduation.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    public Course findByNameAndAndCredit(String name, int credit);
}
