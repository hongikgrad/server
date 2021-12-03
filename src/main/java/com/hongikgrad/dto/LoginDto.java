package com.hongikgrad.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginDto {

    @JsonProperty("USER_ID")
    private String id;

    @JsonProperty("PASSWD")
    private String pw;

    public LoginDto() {
    }

    public LoginDto(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }
}
