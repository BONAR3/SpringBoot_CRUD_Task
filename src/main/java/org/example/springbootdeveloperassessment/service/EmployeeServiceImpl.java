package org.example.springbootdeveloperassessment.service;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbootdeveloperassessment.dto.PartialUpdateDto;
import org.example.springbootdeveloperassessment.exception.EmployeeNotFoundException;
import org.example.springbootdeveloperassessment.model.Employee;
import org.example.springbootdeveloperassessment.repository.EmployeeRepository;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.exception.DuplicateEmailException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Validated
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService{

    private static final BigDecimal INTERN_MIN_SALARY   = new BigDecimal("15000.00");
    private static final BigDecimal DEFAULT_MIN_SALARY  = new BigDecimal("30000.00");

    private final EmployeeRepository repository;
    private final Validator validator;

    @Transactional
    @Override
    public EmployeeResponseDto createEmployee(@Valid EmployeeRequestDto dto) {

        repository.findByEmail(dto.getEmail()).
                ifPresent(e -> {throw new DuplicateEmailException("Email already Exist");
        });

        validateSalary(dto.getDepartment(), dto.getSalary());

        Employee e = repository.save(mapToEntity(dto));

        return mapToDto(e);
    }

    @Override
    @Transactional
    public Page<EmployeeResponseDto> findAll(String department, Boolean active, Pageable pageable) {

        List<Employee> employees = repository.findAll();
        List<EmployeeResponseDto> dtoResponse = employees.stream().map(this::mapToDto).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoResponse.size());
        List<EmployeeResponseDto> page = start >
                dtoResponse.size() ? Collections.emptyList() :
                dtoResponse.subList(start,end);

        return new PageImpl<>(page, pageable, dtoResponse.size());
    }

    @Override
    public EmployeeResponseDto findById(Long id) {

        Employee employee = findId(id);

        return mapToDto(employee);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, @Valid EmployeeRequestDto dto) {

        Employee employee = findId(id);
        checkDuplicateEmail(dto.getEmail(),id);

        validateSalary(dto.getDepartment(), dto.getSalary());

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setSalary(dto.getSalary());
        employee.setDateOfJoining(dto.getDateOfJoining());
        employee.setActive(dto.getActive());

        return mapToDto(repository.save(employee));
    }

    @Override
    public EmployeeResponseDto partialUpdate(Long id, PartialUpdateDto dto) {

        Employee employee = findId(id);

        if (dto.getSalary() != null) {
            String dept = dto.getDepartment() != null ? dto.getDepartment() : employee.getDepartment();
            validateSalary(dept, dto.getSalary());
            employee.setSalary(dto.getSalary());
        }
        if (dto.getDepartment() != null) {
            employee.setDepartment(dto.getDepartment());
        }
        if (dto.getActive() != null) {
            employee.setActive(dto.getActive());
        }

        return mapToDto(repository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findBySalaryRange(BigDecimal min, BigDecimal max) {

        return repository.findBySalaryRange(min, max)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteEmployee(Long id) {

        Employee employee = findId(id);

        employee.setActive(false);
        repository.save(employee);
    }

    @Override
    public void hardDeleteEmployee(Long id) {

        Employee employee = findId(id);

        if (employee.getActive()){
            throw new RuntimeException("Cannot hard delete active employee");
        }
        else repository.delete(employee);
    }


    //Private methods

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
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .salary(dto.getSalary())
                .dateOfJoining(dto.getDateOfJoining())
                .active(dto.getActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
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

    private void checkDuplicateEmail(String email, Long excludeId) {

        Optional<Employee> existing = repository.findByEmail(email);

        existing.ifPresent(e -> {
            if (!e.getId().equals(excludeId)) {
                throw new DuplicateEmailException("Email already exist");
            }
        });
    }

    private Employee findId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not Found"));
    }
}
