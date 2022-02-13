package com.hongikgrad.major.controller;

import com.hongikgrad.major.application.MajorService;
import com.hongikgrad.major.entity.Major;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MajorController {

	private final MajorService majorService;

	@GetMapping("/majors")
	public ResponseEntity MajorGET() {
		try {
			List<Major> majorList = majorService.getEnableMajorList();
			return new ResponseEntity(majorList, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
}
