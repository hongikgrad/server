package com.hongikgrad.major.repository;

import com.hongikgrad.major.dto.MajorDto;
import com.hongikgrad.major.entity.Major;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hongikgrad.major.entity.QMajor.*;
import static com.hongikgrad.major.entity.QMajorHierarchy.*;

@RequiredArgsConstructor
public class MajorHierarchyRepositoryImpl implements MajorHierarchyRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Major> findSlavesByMaster(MajorDto master) {
		// 순환 참조 조심
		return queryFactory
				.select(major)
				.from(majorHierarchy)
				.join(majorHierarchy.master, major)
				.on(majorHierarchy.master.id.eq(master.getId()))
				.fetch();
	}
}
