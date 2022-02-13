package com.hongikgrad.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

    @JsonProperty("USER_ID")
    private String id;

    @JsonProperty("PASSWD")
    private String pw;

    public LoginRequestDto(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }
}
