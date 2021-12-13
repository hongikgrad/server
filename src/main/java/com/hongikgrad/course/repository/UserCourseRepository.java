package com.hongikgrad.course.repository;

import com.hongikgrad.course.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long>, UserCourseRepositoryCustom {
}
