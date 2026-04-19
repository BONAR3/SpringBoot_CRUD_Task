package org.example.springbootdeveloperassessment.service;

import jakarta.validation.Valid;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.dto.PartialUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;


public interface EmployeeService {

    EmployeeResponseDto createEmployee (EmployeeRequestDto dto);

    Page<EmployeeResponseDto> findAll (String department, Boolean active, Pageable pageable);

    EmployeeResponseDto findById(Long id);

    EmployeeResponseDto updateEmployee(Long id, @Valid EmployeeRequestDto dto);

    EmployeeResponseDto partialUpdate(Long id, PartialUpdateDto dto);

    List<EmployeeResponseDto> findBySalaryRange(BigDecimal min, BigDecimal max);

    void softDeleteEmployee (Long id);

    void hardDeleteEmployee (Long id);

}
