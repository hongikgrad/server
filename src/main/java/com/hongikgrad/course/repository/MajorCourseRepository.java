package com.hongikgrad.course.repository;

import com.hongikgrad.course.entity.MajorCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorCourseRepository extends JpaRepository<MajorCourse, Long> {
}
