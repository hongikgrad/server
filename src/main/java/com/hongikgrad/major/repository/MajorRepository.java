package com.hongikgrad.major.repository;

import com.hongikgrad.major.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long>, MajorRepositoryCustom {
	public Major findMajorByName(String name);
	public Major findMajorByNameContains(String name);
	public Major findMajorByCode(String code);

	public Major findMajorById(Long id);
	public List<Major> findAllByEnableTrue();
	public List<Major> findAll();
}
