package com.secure.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secure.entity.Employee;
import com.secure.service.EmployeeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/employees")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

	private final EmployeeService employeeService;
	
	
    public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
    

	@GetMapping("/test")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String test() {
        return "Secure Employee API accessed successfully";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminApi() {
        return "Admin API accessed successfully";
    }
    
 
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Employee> getAllmployee()
    {
    	return employeeService.getAllEmployees();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Employee getEMployeebyId(@PathVariable Long id)
    {
    	return employeeService.getEmployeeById(id);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Employee updateEmployee(@PathVariable Long id,
    								@RequestBody Employee employee)
    {
    	return employeeService.updateEmployee(id, employee);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEmployee(@PathVariable Long id)
    {
    	
    	return employeeService.deleteEmployee(id);
    }
    
    
    
    
    
}
