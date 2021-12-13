package com.hongikgrad.authentication.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="student_id", nullable = false)
    private String studentId;

    public User(String studentId) {
        this.studentId = studentId;
    }
}
