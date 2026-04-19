package org.example.springbootdeveloperassessment.repository;

import org.example.springbootdeveloperassessment.model.Employee;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartment(String department);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByActiveTrue();

    @Query("SELECT e FROM Employee e WHERE e.salary BETWEEN :min AND :max")

    List<Employee> findBySalaryRange(@Param("min")BigDecimal min, @Param("max") BigDecimal max);

}
