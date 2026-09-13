package com.secure.repositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

}
