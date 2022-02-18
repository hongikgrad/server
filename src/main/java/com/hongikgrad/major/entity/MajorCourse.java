package com.hongikgrad.major.entity;

import com.hongikgrad.course.entity.Course;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(
		uniqueConstraints = {
				@UniqueConstraint(
						columnNames = {"course_id", "major_id", "is_required"}
				)
		}
)
public class MajorCourse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "major_id")
	private Major major;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@Column(name = "is_required", columnDefinition = "bit(1) default 0")
	private boolean isRequired;

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

	public void toggleRequired() {
		this.isRequired = !this.isRequired;
	}

	@Override
	public boolean equals(Object a) {
		return (a instanceof MajorCourse) && (((MajorCourse) a).getCourse().equals(this.getCourse())) && ((MajorCourse) a).getMajor() == this.getMajor();
	}

	@Override
	public int hashCode() {
		return (this.getMajor().getName() + this.getCourse()).hashCode();
	}
}
