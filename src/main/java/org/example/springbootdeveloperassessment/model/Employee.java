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
public class Employee {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Size(max=50)
    private String firstName;

    @NotBlank
    @Size(max=50)
    private String lastName;

    @NotBlank
    @Email
    @Column(unique=true)
    private String email;

    @NotBlank
    @Size(max=100)
    private String department;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal salary;

    @NotNull
    @PastOrPresent
    private LocalDate dateOfJoining;

    @NotNull
    private Boolean active;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
