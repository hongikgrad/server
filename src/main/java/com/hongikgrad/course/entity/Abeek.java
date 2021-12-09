package com.hongikgrad.graduation.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class Abeek {

    @Id
    @GeneratedValue
    @Column(name = "abeek_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "abeek_area", nullable = false)
    private String area;
}
