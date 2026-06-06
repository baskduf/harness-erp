package com.example.harnesserp.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEmployeeRequest(@NotBlank String name) {
}
