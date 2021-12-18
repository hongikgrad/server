package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.dto.CourseResponseDto;
import com.hongikgrad.course.entity.Course;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hongikgrad.course.entity.QCourse.course;
import static com.hongikgrad.course.entity.QUserCourse.userCourse;
import static com.querydsl.core.types.Projections.constructor;

@RequiredArgsConstructor
public class UserCourseRepositoryImpl implements UserCourseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CourseDto> findUserTakenCoursesByStudentId(String studentId) {
        return queryFactory
                .select(
                        constructor(
                                CourseDto.class,
                                course.name,
                                course.number,
                                course.abeek,
                                course.credit
                                )
                )
                .from(userCourse)
                .join(userCourse.course, course)
                .where(userCourse.user.studentId.eq(studentId))
                .fetch();
    }

    @Override
    public List<CourseDto> findUserTakenAbeekCoursesByStudentId(String studentId) {
        queryFactory
                .select(
                )
                .from(userCourse)
                .join(userCourse.course, course)
                .where(userCourse.user.studentId.eq(studentId).and(userCourse.course.abeek.length().gt(3)))
                .fetch();
        return null;
    }
}
