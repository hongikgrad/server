package com.hongikgrad.course.repository;

import com.hongikgrad.course.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
}
