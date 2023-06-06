package com.example.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

//@SpringBootTest
class EmployeeServiceIntegrationTest {

//    @TestConfiguration
//    static class EmployeeServiceIntegrationTestContextConfiguration {
//        @Bean
//        public EmployeeService employeeService() {
//            return new EmployeeService(null);
//        }
//    }

//    @Autowired
    private EmployeeService employeeService;

//    @MockBean
    private EmployeeRepository employeeRepository;

//    @BeforeEach
    public void setUp() {

        var employees = List.of(
                new Employee("test employee", 99),
                new Employee("another test employee", 22),
                new Employee("one more test employee", 44)
        );

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);
        Mockito.when(employeeRepository.existsById(anyLong())).thenReturn(false);

        for (var employee : employees) {

            Mockito.when(employeeRepository.existsById(employee.getId()))
                    .thenReturn(true);

            Mockito.when(employeeRepository.findById(employee.getId()))
                    .thenReturn(Optional.of(employee));
        }

    }

//    @Test
    void testFindAll() {
    }

//    @Test
    void testFindById() {
    }

//    @Test
    void testCreate() {
    }

//    @Test
    void testUpdate() {
    }

//    @Test
    void testDeleteById() {
    }
}