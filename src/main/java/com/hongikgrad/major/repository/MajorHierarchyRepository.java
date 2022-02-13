package com.hongikgrad.major.repository;

import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorHierarchyRepository extends JpaRepository<MajorHierarchy, Long>, MajorHierarchyRepositoryCustom {
	List<MajorHierarchy> findAll();
	List<MajorHierarchy> findAllByMaster(Major master);
	List<MajorHierarchy> findAllByMasterId(Long masterId);
	MajorHierarchy findMajorHierarchyByMasterIdAndSlaveId(Long masterId, Long slaveId);
}
