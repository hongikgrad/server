package com.hongikgrad.major.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MajorHierarchy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "major_hierarchy_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "master_id", updatable = false)
	private Major master;

	@ManyToOne
	@JoinColumn(name = "slave_id", updatable = false)
	private Major slave;

	public MajorHierarchy(Major master, Major slave) {
		this.master = master;
		this.slave = slave;
	}
}
