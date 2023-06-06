package com.example.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    private List<Employee> employees;

    @BeforeEach
    public void init() {
        employees = List.of(
                employeeService.create(new Employee("test employee", 99)),
                employeeService.create(new Employee("another employee", 22))
        );
    }

    @Test
    void testFindAll() {
        assertEquals(employees, employeeService.findAll());
    }

    @Test
    void testFindById() {

        for (var employee : employees) {
            assertEquals(employee, employeeService.findById(employee.getId()));
        }

        assertNull(employeeService.findById(999L));
    }

    @Test
    void testCreate() {

        var createdEmployee = new Employee("created employee", 33);
        var createdEmployeePersisted = employeeService.create(createdEmployee);

        assertEquals(createdEmployee.getName(), createdEmployeePersisted.getName());
        assertEquals(createdEmployee.getAge(), createdEmployeePersisted.getAge());

        assertEquals(createdEmployeePersisted, employeeService.findById(createdEmployeePersisted.getId()));

    }

    @Test
    void testUpdate() {

        for (var employee : employees) {

            employee.setName(employee.getName() + "!!");
            employee.setAge((employee.getAge() + 1) * 2);

            assertEquals(employee, employeeService.update(employee.getId(), employee));
            assertEquals(employee, employeeService.findById(employee.getId()));
        }

        assertNull(employeeService.update(999L, employees.get(0)));
    }

    @Test
    void testDeleteById() {

        assertFalse(employeeService.findAll().isEmpty());

        int numberOfDeletedEmployees = 0;

        for (var employee : employees) {

            employeeService.deleteById(employee.getId());
            numberOfDeletedEmployees++;

            assertEquals(employees.size() - numberOfDeletedEmployees, employeeService.findAll().size());
        }

        assertTrue(employeeService.findAll().isEmpty());
    }
}