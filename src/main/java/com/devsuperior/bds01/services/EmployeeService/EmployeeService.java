package com.devsuperior.bds01.services.EmployeeService;

import com.devsuperior.bds01.dto.EmployeeDTO;
import com.devsuperior.bds01.entities.Department;
import com.devsuperior.bds01.entities.Employee;
import com.devsuperior.bds01.repositories.DepartmentRepository.DepartmentRepository;
import com.devsuperior.bds01.repositories.EmployeeRepository.EmployeeRepository;
import com.devsuperior.bds01.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAllPaged(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(EmployeeDTO::new);
    }

    @Transactional
    public EmployeeDTO insert(EmployeeDTO dto) {
        Employee entity = new Employee();
        copyDTOToEntity(dto, entity);
        return new EmployeeDTO(employeeRepository.save(entity));
    }

    @Transactional(readOnly = true)
    private void copyDTOToEntity(EmployeeDTO dto, Employee entity) {
        try {
            Department department = departmentRepository.getOne(dto.getDepartmentId());
            entity.setDepartment(department);
            entity.setEmail(dto.getEmail());
            entity.setName(dto.getName());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Department not defined");
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(String.format("Department with id %d not found",
                    dto.getDepartmentId()));
        }
    }
}
