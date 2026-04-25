package org.example.springbootdeveloperassessment.repository;

import org.example.springbootdeveloperassessment.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByDepartment(String department, Pageable pageable);

    Page<Employee> findByDepartmentAndActive(String department, Boolean active, Pageable pageable);

    Optional<Employee> findByEmail(String email);

    Page<Employee> findByActive(Boolean active, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.salary BETWEEN :min AND :max")

    List<Employee> findBySalaryRange(@Param("min")BigDecimal min, @Param("max") BigDecimal max);

}
