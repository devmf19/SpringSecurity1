package com.consiti.springsecurity1.security.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
@Getter @Setter
public class LoginDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
