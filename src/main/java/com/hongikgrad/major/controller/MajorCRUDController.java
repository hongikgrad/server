package com.hongikgrad.major.controller;

import com.hongikgrad.authentication.application.UserService;
import com.hongikgrad.major.application.MajorService;
import com.hongikgrad.major.dto.MajorRequestDto;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class MajorCRUDController {
	private final MajorRepository majorRepository;

	private final UserService userService;
	private final MajorService majorService;

	@GetMapping("/admin/majors")
	public ResponseEntity majorGET(HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			return new ResponseEntity(majorService.getMajorDtoList(), HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/admin/majors")
	public ResponseEntity majorPOST(@RequestBody MajorRequestDto majorRequestDto, HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			Major savedMajor = majorRepository.save(new Major(majorRequestDto.getName(), majorRequestDto.getCode(), majorRequestDto.getCollege()));
			return new ResponseEntity(savedMajor, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("/admin/majors/{majorId}")
	public ResponseEntity majorPATCH(@PathVariable("majorId") Long majorId, @RequestBody MajorRequestDto majorRequestDto, HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			Major major = majorRepository.findMajorById(majorId);
			System.out.println("major.getName( = " + majorRequestDto.getName());
			major.changeName(majorRequestDto.getName());
			major.changeCode(majorRequestDto.getCode());
			major.changeCollege(majorRequestDto.getCollege());
			System.out.println(majorRequestDto.isEnableToggle());
			if(majorRequestDto.isEnableToggle()) {
				major.toggleEnable();
			}
			majorRepository.save(major);
			return new ResponseEntity(major, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/admin/majors/{majorId}")
	public ResponseEntity majorDELETE(@PathVariable("majorId") Long majorId, HttpServletRequest request) throws AuthenticationException {
		try {
			userService.authenticateAdmin(request);
			Major major = majorRepository.findMajorById(majorId);
			majorRepository.delete(major);
			return new ResponseEntity(HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
}
