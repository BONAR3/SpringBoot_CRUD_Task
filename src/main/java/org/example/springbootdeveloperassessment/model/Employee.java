package org.example.springbootdeveloperassessment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class Employee {

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

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
