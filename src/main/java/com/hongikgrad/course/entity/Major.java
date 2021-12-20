package com.hongikgrad.course.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Long id;

    @Column(name = "major_name")
    private String name;

    @Column(name = "major_code")
    private String code;

    // 단과대
    @Column(name = "college")
    private String college;

    public Major(String name) {
        this.name = name;
    }

    public Major(String name, String college) {
        this.name = name;
        this.college = college;
    }

    public Major(String name, String code, String college) {
        this.name = name;
        this.code = code;
        this.college = college;
    }
}