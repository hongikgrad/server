package com.hongikgrad.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CourseResponseDto {
    private String name;
    private int credit;
    private String number;
}
