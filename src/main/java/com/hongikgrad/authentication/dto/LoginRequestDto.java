package com.hongikgrad.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @JsonProperty("USER_ID")
    private String id;

    @JsonProperty("PASSWD")
    private String pw;

    @JsonProperty("MAJOR")
    private String major;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }
}
