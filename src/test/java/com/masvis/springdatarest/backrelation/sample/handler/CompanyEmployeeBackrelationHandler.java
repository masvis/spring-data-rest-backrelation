package com.masvis.springdatarest.backrelation.sample.handler;

import com.masvis.springdatarest.backrelation.BackrelationHandler;
import com.masvis.springdatarest.backrelation.sample.domain.Company;
import com.masvis.springdatarest.backrelation.sample.domain.Employee;
import com.masvis.springdatarest.backrelation.sample.service.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CompanyEmployeeBackrelationHandler implements BackrelationHandler<Company, Employee> {
    @Autowired
    private final EmployeeRepository employeeRepository;

    public CompanyEmployeeBackrelationHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Class<? extends Employee> supports() {
        return Employee.class;
    }

    @Override
    public Collection<? extends Employee> findDeletablesByEntity(Company updatedEntity, Collection<? extends Employee> finals) {
        Collection<Employee> olds = employeeRepository.findByCompaniesContaining(updatedEntity);
        return olds.stream()
                .filter(ors -> finals.stream()
                        .map(f -> f.getId())
                        .noneMatch(frsid -> ors.getId().equals(frsid)))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Company> getFrontRelation(Employee entity) {
        return entity.getCompanies();
    }
}