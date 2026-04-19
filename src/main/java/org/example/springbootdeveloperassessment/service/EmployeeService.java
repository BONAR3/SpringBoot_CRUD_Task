package org.example.springbootdeveloperassessment.service;

import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto createEmployee (EmployeeRequestDto dto);

    List<EmployeeResponseDto> getAllEmployees(Pageable pageable);

    EmployeeResponseDto getEmployeeById(Long id);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto);

    List<EmployeeResponseDto> findBySalaryRange(BigDecimal min);

    void softDeleteEmployee (Long id);

    void hardDeleteEmployee (Long id);

}
