package com.hongikgrad.course.repository;

import com.hongikgrad.authentication.entity.User;
import com.hongikgrad.course.dto.CourseDto;
import com.hongikgrad.course.entity.Course;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.QMajor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hongikgrad.course.entity.QCourse.course;
import static com.hongikgrad.course.entity.QUserCourse.userCourse;
import static com.hongikgrad.major.entity.QMajorCourse.majorCourse;
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
                                course.credit,
                                course.semester
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

    @Override
    public List<CourseDto> findUserTakenMajorCoursesByStudentId(String studentId, Major studentMajor) {
        return null;
//        String generalMajorName = "";
//        if(studentMajor.getCollege().equals("공과대학")) {
//            generalMajorName = "공대교학과";
//        } else if(studentMajor.getCollege().equals("미술대학")) {
//            generalMajorName = "미대교학과";
//        } else if(studentMajor.getCollege().equals("사범대학")) {
//            generalMajorName = "사대교학과";
//        } else if(studentMajor.getCollege().equals("법과대학")) {
//            generalMajorName = "법과대 교학과";
//        }
//
//        Major generalMajor = queryFactory
//                .selectFrom(QMajor.major)
//                .where(QMajor.major.name.contains(generalMajorName))
//                .fetchOne();
//
//        return queryFactory
//                .select(
//                        constructor(
//                                CourseDto.class,
//                                userCourse.course.name,
//                                userCourse.course.number,
//                                userCourse.course.abeek,
//                                userCourse.course.credit,
//                                userCourse.course.semester
//                        ))
//                .from(userCourse)
//                .join(majorCourse).on(majorCourse.course.id.eq(userCourse.course.id).and(majorCourse.major.eq(studentMajor).or(majorCourse.major.eq(generalMajor))))
//                .where(userCourse.user.studentId.eq(studentId))
//                .fetch();
    }

    @Override
    public boolean existsUserTakenCourse(User user, Course course) {
        return queryFactory
                .from(userCourse)
                .where(userCourse.user.eq(user).and(userCourse.course.eq(course)))
                .fetchFirst() != null;
    }
}
