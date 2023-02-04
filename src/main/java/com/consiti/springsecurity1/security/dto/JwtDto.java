package com.consiti.springsecurity1.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class JwtDto {
    private String token;
}
