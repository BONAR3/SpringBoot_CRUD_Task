package org.example.springbootdeveloperassessment.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.springbootdeveloperassessment.model.Employee;
import org.example.springbootdeveloperassessment.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExportExcelService {

    private final EmployeeRepository repository;

    public void exportEmployees(HttpServletResponse response, String department, Boolean active)
            throws IOException {

        List<Employee> employees = findEmployees(department, active);

        try (Workbook workbook = new XSSFWorkbook()) {

        Sheet sheet = workbook.createSheet("Employees");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        Row headerRow = sheet.createRow(0);


        String[] columns = {"ID", "First Name", "Last Name", "Email", "Department", "Salary", "Date Of Joining",
                "Active", "Created At", "Updated At"};

        for (int index = 0; index < columns.length; index++) {
            Cell cell = headerRow.createCell(index);
            cell.setCellValue(columns[index]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;

        for (Employee emp : employees) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getFirstName());
            row.createCell(2).setCellValue(emp.getLastName());
            row.createCell(3).setCellValue(emp.getEmail());
            row.createCell(4).setCellValue(emp.getDepartment());
            row.createCell(5).setCellValue(emp.getSalary().doubleValue());
            row.createCell(6).setCellValue(emp.getDateOfJoining().toString());
            row.createCell(7).setCellValue(emp.getActive());
            row.createCell(8).setCellValue(emp.getCreatedAt() != null ? emp.getCreatedAt().toString() : "");
            row.createCell(9).setCellValue(emp.getUpdatedAt() != null ? emp.getUpdatedAt().toString() : "");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException("Error exporting Excel file");
        }
    }

    private List<Employee> findEmployees(String department, Boolean active) {

        List<Employee> employees;

        if (department != null && active != null) {
            employees = repository.findByDepartmentAndActive(department, active);
        } else if (department != null) {
            employees = repository.findByDepartment(department);
        } else if (active != null) {
            employees = repository.findByActive(active);
        } else {
            employees = repository.findAll();
        }

        return employees;
    }

  }


