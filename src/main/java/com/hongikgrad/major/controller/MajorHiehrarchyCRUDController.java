package com.hongikgrad.major.controller;

import com.hongikgrad.major.application.MajorService;
import com.hongikgrad.major.entity.Major;
import com.hongikgrad.major.entity.MajorHierarchy;
import com.hongikgrad.major.repository.MajorHierarchyRepository;
import com.hongikgrad.major.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MajorHiehrarchyCRUDController {

	private final MajorHierarchyRepository majorHierarchyRepository;
	private final MajorRepository majorRepository;

	private final MajorService majorService;

	@GetMapping("/admin/majors/{majorId}/hierarchy")
	public ResponseEntity majorsHierarchyGET(@PathVariable("majorId") Long majorId) {
		return new ResponseEntity(majorHierarchyRepository.findAllByMasterId(majorId), HttpStatus.OK);
	}

	@PostMapping("/admin/majors/{masterId}/hierarchy/{slaveId}")
	public ResponseEntity majorsHierarchyPOST(@PathVariable("masterId") Long masterId, @PathVariable("slaveId") Long slaveId) {
		try {
			Major master = majorRepository.findMajorById(masterId);
			Major slave = majorRepository.findMajorById(slaveId);

			MajorHierarchy saved = majorHierarchyRepository.save(new MajorHierarchy(master, slave));
			majorService.mergeMajor(masterId, slaveId);
			return new ResponseEntity(saved, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/admin/majors/{masterId}/hierarchy/{slaveId}")
	public ResponseEntity majorsHiehrarchyDELETE(@PathVariable("masterId") Long masterId, @PathVariable("slaveId") Long slaveId) {
		try {
			MajorHierarchy target = majorHierarchyRepository.findMajorHierarchyByMasterIdAndSlaveId(masterId, slaveId);
			majorHierarchyRepository.delete(target);
			majorService.unMergeMajor(masterId, slaveId);
			return new ResponseEntity(HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}
}
