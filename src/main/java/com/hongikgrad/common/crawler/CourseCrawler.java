package com.hongikgrad.common.crawler;

import com.hongikgrad.course.dto.CourseCrawlingDto;
import com.hongikgrad.course.entity.Course;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static org.jsoup.Connection.Method.*;

@Component
@RequiredArgsConstructor
public class CourseCrawler extends Crawler {
    public Set<CourseCrawlingDto> getCoursesFromTimeTable(Map<String, String> data) throws IOException, IndexOutOfBoundsException {
        Element tbody = getParsedTimeTableBody(data);
        return getCoursesFromTbody(tbody);
    }

    private Set<CourseCrawlingDto> getCoursesFromTbody(Element tbody) throws IndexOutOfBoundsException {
        Set<CourseCrawlingDto> courses = new HashSet<>();
        int courseNumberIndex = getCourseNumberIndex(tbody.child(0));
        String regex1 = "(\\(\\*\\))";
        String regex2 = "(\\(사이버강좌\\))";
        for (int i = 1; i <= tbody.childrenSize() - 3; i++) {
            Element row = tbody.child(i);
            if(!validateRow(row, courseNumberIndex)) return courses;
            if(!validateCourseNumber(row, courseNumberIndex)) continue;
            String courseType = row.child(courseNumberIndex - 1).text();
            String courseNumber = row.child(courseNumberIndex).text().substring(0, 6);
            String courseName = row.child(courseNumberIndex + 1).text()
                    .replaceAll(regex1, "")
                    .replaceAll(regex2, "");
            int courseCredit = Integer.parseInt(row.child(courseNumberIndex + 2).text());
            courses.add(new CourseCrawlingDto(courseName, courseCredit, courseNumber, courseType));
        }
        return courses;
    }

    private int getCourseNumberIndex(Element row) {
        for(int i = 0; i < row.childrenSize(); i++) {
            if(row.child(i).text().equals("학수 번호")) return i;
        }
        // 유효하지 않은 페이지
        throw new IndexOutOfBoundsException();
    }

    private Boolean validateRow(Element row, int courseNumberIndex) {
        return row.childrenSize() >= courseNumberIndex + 2;
    }

    private Boolean validateCourseNumber(Element row, int courseNumberIndex) {
        String courseNumber = row.child(courseNumberIndex).text();
        if(courseNumber.length() < 6) return false;
        return courseNumber.substring(0, 6).matches("[0-9]{6}");
    }

    private Element getParsedTimeTableBody(Map<String, String> data) throws IOException {
        String TIME_TABLE_URL = "https://sugang.hongik.ac.kr/cn50001.jsp";
        return Objects.requireNonNull(getJsoupResponseDocument(TIME_TABLE_URL, null, getHeaders(), data, POST).body().getElementById("select_list")).child(0);
    }
}
