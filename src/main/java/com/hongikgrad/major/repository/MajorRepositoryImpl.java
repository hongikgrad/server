package com.hongikgrad.major.repository;


import com.hongikgrad.major.dto.MajorDto;
import com.hongikgrad.major.entity.Major;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hongikgrad.major.entity.QMajor.major;
import static com.hongikgrad.major.entity.QMajorHierarchy.*;

@RequiredArgsConstructor
public class MajorRepositoryImpl implements MajorRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<MajorDto> findAllMajorDto() {
		List<MajorDto> majorDtoList = queryFactory
				.select(
						Projections.constructor(
								MajorDto.class,
								major.id,
								major.name,
								major.college,
								major.code,
								major.enable
						))
				.from(major)
				.fetch();

		for (MajorDto majorDto : majorDtoList) {
			List<Major> slaveList = findSlaveById(majorDto.getId());
			majorDto.addSlave(slaveList);
		}

		return majorDtoList;
	}

	private List<Major> findSlaveById(Long id) {
		return queryFactory
				.select(majorHierarchy.slave)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.id.eq(id))
				.fetch();
	}
}
