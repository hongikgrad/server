package com.hongikgrad.graduation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraudationController {

    /* 유저가 졸업요건을 만족하는지 여부를 응답 */
    @GetMapping("users/graduation")
    public void userGraduation() {

    }
}
