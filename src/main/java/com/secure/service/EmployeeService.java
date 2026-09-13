package com.secure.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.secure.entity.Employee;
import com.secure.repositry.EmployeeRepository;

@Service
public class EmployeeService {
	
	private final EmployeeRepository empRepo;

	public EmployeeService(EmployeeRepository empRepo) {
		this.empRepo = empRepo;
	}
	
	public Employee createEmployee(Employee employee)
	{
		return empRepo.save(employee);
	}

	public List<Employee> getAllEmployees() {
        return empRepo.findAll();
    }
	
	public Employee getEmployeeById(Long id)
	{
		return empRepo.findById(id).orElseThrow(
				()-> new RuntimeException("Employee not found with id: " + id));
	}

	public Employee updateEmployee(Long id,Employee employee)
	{
		Employee existEmployee=getEmployeeById(id);
		
		existEmployee.setName(employee.getName());
		existEmployee.setEmail(employee.getEmail());
		existEmployee.setDepartment(employee.getDepartment());
		existEmployee.setSalary(employee.getSalary());

        return empRepo.save(existEmployee);
		
	}
	
	public String deleteEmployee(Long id) {

        Employee employee = getEmployeeById(id);

        empRepo.delete(employee);

        return "Employee deleted successfully";
    }
}
