package org.example.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ResponseErrorDto(
        String error,
        LocalDate timestamp,
        String path
) {}
