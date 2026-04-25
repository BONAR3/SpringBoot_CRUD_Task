package org.example.springbootdeveloperassessment.service;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbootdeveloperassessment.dto.PartialUpdateDto;
import org.example.springbootdeveloperassessment.exception.EmployeeNotFoundException;
import org.example.springbootdeveloperassessment.model.Department;
import org.example.springbootdeveloperassessment.model.Employee;
import org.example.springbootdeveloperassessment.repository.EmployeeRepository;
import org.example.springbootdeveloperassessment.dto.EmployeeRequestDto;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.exception.DuplicateEmailException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public EmployeeResponseDto createEmployee(EmployeeRequestDto dto) {

        repository.findByEmail(dto.getEmail()).
                ifPresent(e -> {throw new DuplicateEmailException("Email already Exist");
        });

        validateSalary(dto.getDepartment(), dto.getSalary());

        Employee e = repository.save(mapToEntity(dto));

        return mapToDto(e);
    }

    @Override
    @Transactional
    public Page<EmployeeResponseDto> findAll(String department, Boolean active, String direction,
                                             String sortBy, int page, int size) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page <Employee> employees;

        if (department != null && active != null) {
            employees = repository.findByDepartmentAndActive(department, active, pageable);
        } else if (department != null) {
            employees = repository.findByDepartment(department, pageable);
        } else if (active != null) {
            employees = repository.findByActive(active, pageable);
        } else
            employees = repository.findAll(pageable);

         return employees.map(this::mapToDto);

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
            if (dto.getDepartment() == null){
                validateSalary(employee.getDepartment(), dto.getSalary());
            }
            validateSalary(dept, dto.getSalary());
            employee.setSalary(dto.getSalary());
        }
        if (dto.getDepartment() != null) {
            employee.setDepartment(dto.getDepartment());
        }
        if (dto.getActive() != null) {
            employee.setActive(dto.getActive());
        }

        employee.setUpdatedAt(LocalDateTime.now());

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

        validateDepartment(department);
        boolean acceptsInterns = Department.acceptsInterns(department);

        if (salary == null) {
            throw new RuntimeException("Salary cannot be null");
        }

        BigDecimal minSalary = acceptsInterns
                ? new BigDecimal("15000")
                : new BigDecimal("30000");

        if (salary.compareTo(minSalary) < 0) {
            throw new RuntimeException(
                    "Minimum salary for " + department + " is " + minSalary);
        }
    }

    private Employee mapToEntity(EmployeeRequestDto dto) {
        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .salary(dto.getSalary())
                .dateOfJoining(LocalDate.now())
                .active(dto.getActive())
                .createdAt(LocalDateTime.now())
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

    private void validateDepartment(String department){

        if (!Department.exists(department)) {
            throw new RuntimeException("Department not found");
        }
    }

}