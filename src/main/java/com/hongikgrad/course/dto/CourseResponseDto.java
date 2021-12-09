package com.hongikgrad.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {
    private String name;
    private int credit;
    private String number;
}
