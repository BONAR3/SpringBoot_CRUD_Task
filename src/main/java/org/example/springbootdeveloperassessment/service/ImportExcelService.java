package org.example.springbootdeveloperassessment.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.ImportResultDto;
import org.example.springbootdeveloperassessment.exception.ExcelProcessingException;
import org.example.springbootdeveloperassessment.exception.InvalidFileFormatException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class ImportExcelService {

    private final EmployeeServiceImpl employeeService;
    private final Validator validator;

    public ImportResultDto importExcelFile(MultipartFile file) {

        if (file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new InvalidFileFormatException("Only .xlsx files are supported");
        }

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                try {
                    EmployeeRequestDto dto = mapRowToDto(row);

                    Set <ConstraintViolation<EmployeeRequestDto>> violations = validator.validate(dto);

                    if (!violations.isEmpty()) {
                        for (ConstraintViolation<EmployeeRequestDto> v : violations) {
                            errors.add("Row " + (rowIndex + 1) + ": " + v.getPropertyPath() + " - " + v.getMessage());
                        }
                        failureCount++;
                        continue;
                    }

                    employeeService.createEmployee(dto);
                    successCount++;

                } catch (Exception e) {
                    errors.add("Row " + (rowIndex + 1) + ": " + e.getMessage());
                    failureCount++;
                }
            }

        } catch (Exception e) {
            throw new ExcelProcessingException("Error processing Excel file");
        }

        return ImportResultDto.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .errors(errors)
                .build();
    }

    private EmployeeRequestDto mapRowToDto(Row row) {

        EmployeeRequestDto dto = new EmployeeRequestDto();

        dto.setFirstName(getCellString(row, 0));
        dto.setLastName(getCellString(row, 1));
        dto.setEmail(getCellString(row, 2));
        dto.setDepartment(getCellString(row, 3));

        Cell salaryCell = row.getCell(4);
        if (salaryCell != null) {
            if (salaryCell.getCellType() == CellType.NUMERIC)
                dto.setSalary(BigDecimal.valueOf(salaryCell.getNumericCellValue()));
            else dto.setSalary(new BigDecimal (salaryCell.toString()));
        }

        Cell dateCell = row.getCell(5);
        if (salaryCell != null) {
            if (dateCell.getCellType() == CellType.NUMERIC)
                dto.setDateOfJoining(dateCell.getLocalDateTimeCellValue().toLocalDate());
            else dto.setDateOfJoining(LocalDate.parse(dateCell.toString()));
        }

        Cell activeCell = row.getCell(6);
        if (activeCell != null) {
            if (activeCell.getCellType() == CellType.BOOLEAN)
                dto.setActive(activeCell.getBooleanCellValue());
            else dto.setActive(Boolean.parseBoolean(activeCell.toString().toUpperCase()));
        }

        return dto;
    }



    //Converting cell types to match types in or DB
    private String getCellString(Row row, int index){

        Cell cell = row.getCell(index);
        if (cell == null) return null;

        return switch (cell.getCellType()){
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> cell.toString().trim();
        };
    }


}
