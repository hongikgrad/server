package com.hongikgrad.major.repository;

import com.hongikgrad.major.dto.MajorDto;
import com.hongikgrad.major.entity.Major;

import java.util.List;

public interface MajorHierarchyRepositoryCustom {
	List<Major> findSlavesByMaster(MajorDto master);
}
