package org.example.springbootdeveloperassessment.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {

    @Id
    @GeneratedValue
    Long id;

    @NotBlank
    @Size(max=50)
    String firstName;

    @NotBlank
    @Size(max=50)
    String lastName;

    @NotBlank
    @Email
    @Column(unique=true)
    String email;

    @NotBlank
    @Size(max=100)
    String department;

    @NotNull
    @DecimalMin("0.00")
    BigDecimal salary;

    @NotNull
    @PastOrPresent
    LocalDate dateOfJoining;

    @NotNull
    Boolean active;

    @Column(updatable = false)
    LocalDateTime createdAt;

    LocalDateTime updatedAt;

}
