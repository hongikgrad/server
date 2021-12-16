package com.hongikgrad.course.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(
    uniqueConstraints={
        @UniqueConstraint(
                columnNames={"course_number","course_credit"}
        )
	}
)
public class Course {

    @Id
    @GeneratedValue
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String name;

    @Column(name = "course_credit", nullable = false)
    private int credit;

    @Column(name = "course_number", nullable = false)
    private String number;

    @Column(name = "course_abeek")
    private String abeek;

    public Course(String name, int credit, String number) {
        this.name = name;
        this.credit = credit;
        this.number = number;
        this.abeek = "none";
    }

    public Course(String name, int credit, String number, String abeek) {
        this.name = name;
        this.credit = credit;
        this.number = number;
        this.abeek = abeek;
    }
}