package org.example.springbootdeveloperassessment.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record EmployeeResponseDto(Long id,
                                  String firstName,
                                  String lastName,
                                  String email,
                                  String department,
                                  BigDecimal salary,
                                  LocalDate dateOfJoining,
                                  Boolean active,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {
}
