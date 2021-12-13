package com.hongikgrad.common.crawler;

import com.hongikgrad.course.entity.Course;
import com.hongikgrad.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.jsoup.Connection.Method.*;

@Component
@RequiredArgsConstructor
public class CourseCrawler extends Crawler {

    private final CourseRepository courseRepository;

    public List<Course> getAbeekCoursesFromTimeTable(Map<String, String> data) throws IOException, IndexOutOfBoundsException {
        Element tbody = getParsedTimeTableBody(data);
        return getCoursesFromTableBody(tbody, 5);
    }

    public List<Course> getNonAbeekCoursesFromTimeTable(Map<String, String> data) throws IOException, IndexOutOfBoundsException {
        Element tbody = getParsedTimeTableBody(data);
        return getCoursesFromTableBody(tbody, 4);
    }

    private List<Course> getCoursesFromTableBody(Element tbody, int classNumberIndex) throws IndexOutOfBoundsException {
        List<Course> courses = new ArrayList<>();
        String regex1 = "(\\(\\*\\))";
        String regex2 = "(\\(사이버강좌\\))";
        for (int i = 1; i <= tbody.childrenSize() - 3; i++) {
            Element row = tbody.child(i);
            String classNumber = row.child(classNumberIndex).text().substring(0, 6);
            String className = row.child(classNumberIndex + 1).text()
                    .replaceAll(regex1, "")
                    .replaceAll(regex2, "");
            int classCredit = Integer.parseInt(row.child(classNumberIndex + 2).text());
            courses.add(new Course(className, classCredit, classNumber));
        }
        return courses;
    }

    private Element getParsedTimeTableBody(Map<String, String> data) throws IOException {
        String TIME_TABLE_URL = "https://sugang.hongik.ac.kr/cn50001.jsp";
        return Objects.requireNonNull(getJsoupResponseDocument(TIME_TABLE_URL, null, getHeaders(), data, POST).body().getElementById("select_list")).child(0);
    }
}
