package com.hongikgrad.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RequirementDto {
	private String mainField;
	private Integer totalCredit;
	private String briefing;
	private Boolean isSatisfied;
	private List<SubField> subField;

	public RequirementDto(String mainField, Integer totalCredit, String briefing, Boolean isSatisfied) {
		this.mainField = mainField;
		this.totalCredit = totalCredit;
		this.briefing = briefing;
		this.isSatisfied = isSatisfied;
	}

	public RequirementDto(String mainField, Integer totalCredit, String briefing, Boolean isSatisfied, SubField subField) {
		this.mainField = mainField;
		this.totalCredit = totalCredit;
		this.briefing = briefing;
		this.isSatisfied = isSatisfied;
		this.subField = List.of(subField);
	}
}
