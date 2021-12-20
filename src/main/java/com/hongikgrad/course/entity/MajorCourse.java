package com.hongikgrad.course.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MajorCourse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "major_id")
	private Major major;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "course_id")
	private Course course;

	@Column(name = "is_required", columnDefinition = "tinyint(1) default 0")
	private Boolean isRequired;

	public MajorCourse(Major major, Course course) {
		this.major = major;
		this.course = course;
		this.isRequired = false;
	}

	public MajorCourse(Major major, Course course, Boolean isRequired) {
		this.major = major;
		this.course = course;
		this.isRequired = isRequired;
	}
}
