package com.spring.guardadoc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ResponseDTO(

        @NotBlank
        @Size(max = 100)
        String nome,

        @NotBlank
        String token,

        @NotEmpty
        Set<String> roles,

        @NotNull
        Long id) {
}
