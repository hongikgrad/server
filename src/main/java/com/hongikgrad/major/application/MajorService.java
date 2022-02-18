package com.hongikgrad.major.application;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.major.dto.MajorDto;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorCourse;
import com.hongikgrad.major.repository.MajorCourseRepository;
import com.hongikgrad.major.repository.MajorHierarchyRepository;
import com.hongikgrad.major.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorService {

	private final MajorRepository majorRepository;
	private final MajorCourseRepository majorCourseRepository;
	private final MajorHierarchyRepository majorHierarchyRepository;

	public List<MajorDto> getMajorDtoList() {
		List<MajorDto> majorDtoList = majorRepository.findAllMajorDto();
		return majorDtoList;
	}

	public List<Major> getEnableMajorList() {
		return majorRepository.findAllByEnableTrue();
	}
}
