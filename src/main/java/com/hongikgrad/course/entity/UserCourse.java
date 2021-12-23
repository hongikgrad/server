package com.hongikgrad.course.entity;

import com.hongikgrad.authentication.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"user_id","course_id"}
                )
        }
)
public class UserCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_course_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    public UserCourse(User user, Course course) {
        this.user = user;
        this.course = course;
    }
}
