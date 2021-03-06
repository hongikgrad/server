package com.hongikgrad.authentication.controller;

import com.hongikgrad.authentication.application.UserService;
import com.hongikgrad.authentication.dto.LoginRequestDto;
import com.hongikgrad.course.exception.InvalidDocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping(value = "/auth/token", produces = "application/json; charset=UTF-8")
	public ResponseEntity loginV2(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
		try {
			String adminID = System.getenv("ADMIN_ID");
			String adminPW = System.getenv("ADMIN_PASSWORD");

			if (loginRequestDto.getId().equals("test") && loginRequestDto.getPw().equals("1234")) {
				userService.testLogin(response);
			} else if (loginRequestDto.getId().equals(adminID) && loginRequestDto.getPw().equals(adminPW)) {
				userService.adminLogin(response);
			} else {
				return new ResponseEntity<>(userService.loginV2(loginRequestDto), HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		} catch(NullPointerException e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (NoSuchAlgorithmException e) {
			return new ResponseEntity<String>("Invalid Student ID", HttpStatus.BAD_REQUEST);
		} catch (InvalidDocumentException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
		}
	}

//	@PostMapping(value = "/auth/token", produces = "application/json; charset=UTF-8")
	public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
		try {
			String adminID = System.getenv("ADMIN_ID");
			String adminPW = System.getenv("ADMIN_PASSWORD");

			if (loginRequestDto.getId().equals("test") && loginRequestDto.getPw().equals("1234")) {
				userService.testLogin(response);
			} else if (loginRequestDto.getId().equals(adminID) && loginRequestDto.getPw().equals(adminPW)) {
				userService.adminLogin(response);
			} else {
				userService.login(loginRequestDto, response);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		} catch(NullPointerException e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (NoSuchAlgorithmException e) {
			return new ResponseEntity<String>("Invalid Student ID", HttpStatus.BAD_REQUEST);
		} catch (InvalidDocumentException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
		}
	}

	@DeleteMapping(value = "/auth/token")
	public ResponseEntity<String> logout(HttpServletResponse response) {
		return null;
	}
}