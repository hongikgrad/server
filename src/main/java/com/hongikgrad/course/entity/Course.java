package com.hongikgrad.course.entity;

import com.hongikgrad.course.dto.CrawlingCourseDto;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "latest_semester")
    private String semester;

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

    public Course(String name, int credit, String number, String abeek, String semester) {
        this.name = name;
        this.credit = credit;
        this.number = number;
        this.abeek = abeek;
        this.semester = semester;
    }

    public void changeSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public boolean equals(Object a) {
        return (a instanceof Course) && (((Course) a).getNumber().equals(this.getNumber())) && ((Course) a).getCredit() == this.getCredit();
    }

    @Override
    public int hashCode() {
        return (this.getNumber() + this.getCredit()).hashCode();
    }
}