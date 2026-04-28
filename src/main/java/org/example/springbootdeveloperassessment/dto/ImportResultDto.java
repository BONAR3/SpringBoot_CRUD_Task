package org.example.springbootdeveloperassessment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportResultDto {

    private int successCount;
    private int failureCount;
    private List<String> errors;

}
