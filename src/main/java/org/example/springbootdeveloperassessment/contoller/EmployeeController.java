package org.example.springbootdeveloperassessment.contoller;

import lombok.RequiredArgsConstructor;
import org.example.springbootdeveloperassessment.dto.EmployeeResponseDto;
import org.example.springbootdeveloperassessment.service.EmployeeService;
import org.example.springbootdeveloperassessment.service.EmployeeServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity <EmployeeResponseDto> getAllEmployees(@RequestParam() int pageNo,
                                                                @RequestParam() int pageSize){
        PageRequest.of(pageNo,pageSize);
        return EmployeeResponseDto

    }


}
