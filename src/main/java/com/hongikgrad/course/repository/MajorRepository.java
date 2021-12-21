package com.hongikgrad.course.repository;

import com.hongikgrad.course.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
	public Major findMajorByName(String name);
	public Major findMajorByNameContains(String name);
	public Major findMajorByCode(String code);
}
