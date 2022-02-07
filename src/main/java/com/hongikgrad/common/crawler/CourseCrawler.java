package com.hongikgrad.common.crawler;

import com.hongikgrad.course.dto.CrawlingCourseDto;
import com.hongikgrad.course.dto.CrawlingCourseListDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static org.jsoup.Connection.Method.*;

@Component
@RequiredArgsConstructor
public class CourseCrawler extends Crawler {
    public CrawlingCourseListDto getCoursesFromTimeTable(Map<String, String> data) throws IOException, IndexOutOfBoundsException {
        Element tbody = getParsedTimeTableBody(data);
        return getCoursesFromTbody(tbody);
    }

    private CrawlingCourseListDto getCoursesFromTbody(Element tbody) throws IndexOutOfBoundsException {
        Set<CrawlingCourseDto> courses = new HashSet<>();
        Set<String> majors = new HashSet<>();
        int courseNumberIndex = getCourseNumberIndex(tbody.child(0));
        String regex1 = "(\\(\\*\\))";
        String regex2 = "(\\(사이버강좌\\))";
        for (int i = 1; i <= tbody.childrenSize() - 3; i++) {
            Element row = tbody.child(i);
            if(!validateRow(row, courseNumberIndex)) continue;
            if(!validateCourseNumber(row, courseNumberIndex)) continue;
            int courseYear = Integer.parseInt(row.child(0).text());
            String courseMadeBy = row.child(1).text();
            String courseSuperviseBy = row.child(2).text();
            String courseType = row.child(courseNumberIndex - 1).text();
            String courseNumber = row.child(courseNumberIndex).text().substring(0, 6);
            String courseName = row.child(courseNumberIndex + 1).text()
                    .replaceAll(regex1, "")
                    .replaceAll(regex2, "");
            int courseCredit = Integer.parseInt(row.child(courseNumberIndex + 2).text());
            courses.add(new CrawlingCourseDto(courseName, courseCredit, courseNumber, courseType, courseMadeBy, courseSuperviseBy, courseYear));
            majors.add(courseMadeBy);
        }
        return new CrawlingCourseListDto(courses, majors);
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
