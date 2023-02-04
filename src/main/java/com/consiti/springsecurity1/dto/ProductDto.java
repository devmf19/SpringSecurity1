package com.consiti.springsecurity1.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class ProductDto {
    @NotBlank
    private String name;

    @Min(0)
    private Float price;

    public ProductDto(String name, @Min(0) Float price) {
        this.name = name;
        this.price = price;
    }
}
