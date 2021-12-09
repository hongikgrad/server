package com.hongikgrad.crawler;

import com.hongikgrad.crawler.Crawler;
import com.hongikgrad.graduation.dto.CourseResponseDto;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserCourseCrawler extends Crawler {
    String TAKEN_COURSE_URL = "https://cn.hongik.ac.kr/stud/P/01000/01000.jsp";

    public List<CourseResponseDto> getUserTakenCoursesFromClassnet(HttpServletRequest request) throws IOException {
        Document document = getJsoupResponseDocument(TAKEN_COURSE_URL, extractCookie(request), getHeaders(), null, Connection.Method.POST);
        List<CourseResponseDto> courses = new ArrayList<>();
        Elements semesters = document.getElementById("body").select(".table0");
        for (Element gradeTable : semesters) {
            Elements rows = gradeTable.selectFirst("tbody").children();
            for (Element row : rows) {
                Elements classInfo = row.children();
                System.out.println("classInfo = " + classInfo);
                if (row.nextElementSibling() == null) continue;
                if (isTakenClass(classInfo)) continue;
                    String courseNumber = classInfo.first().text();
                    String courseName = classInfo.first().nextElementSibling().text();
                    String credit = classInfo.last().previousElementSibling().previousElementSibling().text();
                    courses.add(new CourseResponseDto(courseName, credit, courseNumber));
            }
        }
        return courses;
    }

    private boolean isTakenClass(Elements subjectInfo) {
        return subjectInfo.last().text().equals("재수강") || subjectInfo.last().previousElementSibling().text().equals("F");
    }

}
