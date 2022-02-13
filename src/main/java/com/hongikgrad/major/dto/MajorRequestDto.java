package com.hongikgrad.major.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MajorRequestDto {
	@JsonProperty("name")
	private String name;

	@JsonProperty("code")
	private String code;

	@JsonProperty("college")
	private String college;

	@JsonProperty("enableToggle")
	private boolean enableToggle = false;
}