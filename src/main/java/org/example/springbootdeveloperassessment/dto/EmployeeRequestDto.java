package org.example.springbootdeveloperassessment.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record EmployeeRequestDto(Long id,
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
