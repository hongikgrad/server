package com.hongikgrad.major.dto;

import com.hongikgrad.major.entity.Major;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MajorDto {
	private Long id;
	private String name;
	private String college;
	private String code;
	private List<Major> slaveList = new ArrayList<>();
	private boolean enable;

//	@QueryProjection
//	public MajorDto(Long id, String name, String college) {
//		this.id = id;
//		this.name = name;
//		this.college = college;
//	}


	public MajorDto(Long id, String name, String college, String code, boolean enable) {
		this.id = id;
		this.name = name;
		this.college = college;
		this.code = code;
		this.enable = enable;
	}

	public void addSlave(List<Major> slaveList) {
		this.slaveList.addAll(slaveList);
	}

	public void addSlave(Major slave) {
		this.slaveList.add(slave);
	}
}
