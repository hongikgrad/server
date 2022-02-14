package com.hongikgrad.major.entity;

import com.querydsl.core.annotations.QueryProjection;
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

    @Column(name = "major_name", unique = true)
    private String name;

    @Column(name = "major_code")
    private String code;

    // 단과대
    @Column(name = "college")
    private String college;

    @Column(name = "enable", columnDefinition = "bit(1) default 0")
    private boolean enable;

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

    public void changeName(String name) {
        if(name == null) return;
        this.name = name;
    }

    public void changeCode(String code) {
        if(code == null) return;
        this.code = code;
    }

    public void changeCollege(String college) {
        if(college == null) return;
        this.college = college;
    }

    public void toggleEnable() {
        this.enable = !this.enable;
        System.out.println("######");
        System.out.println("id = " + this.enable);
    }

    @QueryProjection
    public Major(Long id, String name, String code, String college, Boolean enable) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.college = college;
        this.enable = enable;
    }
}