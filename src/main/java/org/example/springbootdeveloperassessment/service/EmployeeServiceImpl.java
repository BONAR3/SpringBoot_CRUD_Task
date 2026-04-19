package org.example.springbootdeveloperassessment.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloperassessment.exception.EmployeeNotFoundException;
import org.example.springbootdeveloperassessment.model.Employee;
import org.example.springbootdeveloperassessment.repository.EmployeeRepository;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.exception.DuplicateEmailException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

import static java.util.Locale.filter;

@Service
@RequiredArgsConstructor
@Validated
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository repository;

    @Override
    public EmployeeResponseDto createEmployee(@Valid EmployeeRequestDto dto) {

        repository.findByEmail(dto.email()).
                ifPresent(e -> {throw new DuplicateEmailException("Email already Exist");
        });

        validateSalary(dto.department(), dto.salary());

    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees(Pageable pageable) {

        return repository.findAllEmployees(pageable).map(this::mapToDto);

    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {

        Employee e = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        return mapToDto(e);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, @Valid EmployeeRequestDto dto) {

        Employee e  = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        repository.findByEmail(dto.email())
                .ifPresent(e -> {
                    if (!e.getId().equals(id)){
                        throw new DuplicateEmailException("Email already exists");}
        });

        validateSalary(dto.department(), dto.salary());

        e.setFirstName(dto.firstName());
        e.setLastName(dto.lastName());
        e.setEmail(dto.email());
        e.setDepartment(dto.department());
        e.setSalary(dto.salary());
        e.setDateOfJoining(dto.dateOfJoining());
        e.setActive(dto.active());

        return mapToDto(repository.save(e));
    }

    @Override
    public List<EmployeeResponseDto> findBySalaryRange(BigDecimal min) {


    }

    @Override
    public void softDeleteEmployee(Long id) {

        Employee e = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not Found"));

        e.setActive(false);
        repository.save(e);
    }

    @Override
    public void hardDeleteEmployee(Long id) {

        Employee e = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not Found"));

        if (e.getActive()){
            throw new RuntimeException("Cannot hard delete active employee");
        }
        else repository.delete(e);

    }

    private void validateSalary(String department, BigDecimal salary){

        if (salary == null)
            throw new RuntimeException("Salary is empty");

        if (department.equalsIgnoreCase("Intern")) {
            if (salary.compareTo(new BigDecimal(15000)) < 0)
                throw new RuntimeException("Minimum salary is 15000");
        } else
            if (salary.compareTo(new BigDecimal(30000)) < 0)
                throw new RuntimeException("Minimum salary is 30000");
    }

    private Employee mapToEntity(EmployeeRequestDto dto) {
        return Employee.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .department(dto.department())
                .salary(dto.salary())
                .dateOfJoining(dto.dateOfJoining())
                .active(dto.active())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .build();
    }

    private EmployeeResponseDto mapToDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .dateOfJoining(employee.getDateOfJoining())
                .active(employee.getActive())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

}
