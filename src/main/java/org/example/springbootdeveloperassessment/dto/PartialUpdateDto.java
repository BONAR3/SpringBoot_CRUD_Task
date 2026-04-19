package org.example.springbootdeveloperassessment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialUpdateDto {

    @DecimalMin(value = "0.00")
    private BigDecimal salary;

    @Size(max = 100)
    private String department;

    private Boolean active;
}
