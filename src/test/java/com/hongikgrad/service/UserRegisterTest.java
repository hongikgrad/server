package com.hongikgrad.service;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.authentication.repository.UserRepository;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.entity.UserCourse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class UserRegisterTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Test
    public void 회원정보저장하기() {
        User user = new User("b615125");
        em.persist(user);

        Course course1 = new Course("name1", 1, "110011");
        Course course2 = new Course("name2", 2, "110012");
        Course course3 = new Course("name3", 3, "110013");
        em.persist(course1);
        em.persist(course2);
        em.persist(course3);

        UserCourse userCourse1 = new UserCourse(user, course1);
        UserCourse userCourse2 = new UserCourse(user, course2);
        UserCourse userCourse3 = new UserCourse(user, course3);
        em.persist(userCourse1);
        em.persist(userCourse2);
        em.persist(userCourse3);
    }
}
