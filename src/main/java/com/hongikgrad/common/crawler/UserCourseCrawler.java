package com.hongikgrad.common.crawler;

import com.hongikgrad.course.dto.CourseResponseDto;
import com.hongikgrad.course.exception.InvalidDocumentException;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserCourseCrawler extends Crawler {
    String TAKEN_COURSE_URL = "https://cn.hongik.ac.kr/stud/P/01000/01000.jsp";

    public List<CourseResponseDto> getUserTakenCoursesFromClassnet(HttpServletRequest request) throws IOException, NullPointerException, InvalidDocumentException {
        Document document = getJsoupResponseDocument(TAKEN_COURSE_URL, extractCookie(request), getHeaders(), null, Connection.Method.POST);
        List<CourseResponseDto> courses = new ArrayList<>();
        Elements semesters = Objects.requireNonNull(document.getElementById("body")).select(".table0");
        for (Element gradeTable : semesters) {
            Elements rows = Objects.requireNonNull(gradeTable.selectFirst("tbody")).children();
            for (Element row : rows) {
                Elements classInfo = row.children();
                if (row.nextElementSibling() == null) continue;
                if (isTakenClass(classInfo)) continue;
                String courseNumber = Objects.requireNonNull(classInfo.first()).text();
                String courseName = Objects.requireNonNull(Objects.requireNonNull(classInfo.first()).nextElementSibling()).text();
                int credit = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(classInfo.last()).previousElementSibling()).previousElementSibling().text());
                courses.add(new CourseResponseDto(courseName, credit, courseNumber));
            }
        }
        return courses;
    }

    private boolean isTakenClass(Elements subjectInfo) {
        return Objects.requireNonNull(subjectInfo.last()).text().equals("재수강") || Objects.requireNonNull(Objects.requireNonNull(subjectInfo.last()).previousElementSibling()).text().equals("F");
    }

}
