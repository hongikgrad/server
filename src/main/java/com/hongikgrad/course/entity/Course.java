package com.hongikgrad.graduation.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
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

    public Course(String name, int credit, String number) {
        this.name = name;
        this.credit = credit;
        this.number = number;
    }
}