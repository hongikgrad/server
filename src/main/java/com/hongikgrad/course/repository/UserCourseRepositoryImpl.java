package com.hongikgrad.course.repository;

import com.hongikgrad.course.dto.CourseResponseDto;
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
    public List<CourseResponseDto> findUserTakenCoursesByStudentId(String studentId) {
        return queryFactory
                .select(
                        constructor(
                                CourseResponseDto.class,
                                course.name,
                                course.credit,
                                course.number)
                )
                .from(userCourse)
                .join(userCourse.course, course)
                .where(userCourse.user.studentId.eq(studentId))
                .fetch();
    }
}
