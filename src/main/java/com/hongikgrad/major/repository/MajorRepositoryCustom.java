package com.hongikgrad.major.repository;

import com.hongikgrad.major.dto.MajorDto;

import java.util.List;

public interface MajorRepositoryCustom {
	List<MajorDto> findAllMajorDto();
}
