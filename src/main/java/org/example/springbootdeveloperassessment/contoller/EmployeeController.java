package org.example.springbootdeveloperassessment.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.dto.PartialUpdateDto;
import org.example.springbootdeveloperassessment.service.EmployeeService;
import org.example.springbootdeveloperassessment.service.EmployeeServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(dto));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDto>> getAll(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(employeeService.findAll(department, active, pageable));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok(employeeService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequestDto dto){

        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> partialUpdate(
            @PathVariable Long id, @Valid @RequestBody PartialUpdateDto dto){

        return ResponseEntity.ok(employeeService.partialUpdate(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {

        employeeService.softDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        employeeService.hardDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/salary-range")
    public ResponseEntity<List<EmployeeResponseDto>> getBySalaryRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(employeeService.findBySalaryRange(min, max));
    }

}

