package org.example.springbootdeveloperassessment.contoller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.dto.ImportResultDto;
import org.example.springbootdeveloperassessment.dto.PartialUpdateDto;
import org.example.springbootdeveloperassessment.service.EmployeeService;
import org.example.springbootdeveloperassessment.service.ExportExcelService;
import org.example.springbootdeveloperassessment.service.ImportExcelService;
import org.example.springbootdeveloperassessment.service.PdfExportService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;
    private final ImportExcelService importService;
    private final ExportExcelService exportService;
    private final PdfExportService pdfExportService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createEmployee(dto));
    }

    @GetMapping
    public ResponseEntity <Page<EmployeeResponseDto>> getAll(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(service.findAll(department, active, sortDirection, sortBy, page, size));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequestDto dto){

        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmployeeResponseDto> partialUpdate(@PathVariable Long id,
                                                             @Valid @RequestBody PartialUpdateDto dto){

        return ResponseEntity.ok(service.partialUpdate(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {

        service.softDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        service.hardDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/salary-range")
    public ResponseEntity<List<EmployeeResponseDto>> getBySalaryRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(service.findBySalaryRange(min, max));
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResultDto> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importService.importExcelFile(file));
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean active) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        String filename = "employees_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        exportService.exportEmployees(response, department, active);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Download employee report as PDF")
    public void exportToPdf(HttpServletResponse response) {

        pdfExportService.export(response);
    }


}

