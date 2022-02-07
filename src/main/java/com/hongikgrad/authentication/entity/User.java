package com.hongikgrad.authentication.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="student_id", nullable = false)
    private String studentId;

    public User(String studentId) {
        this.studentId = studentId;
    }
}
