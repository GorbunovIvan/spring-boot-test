package com.example.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
// You may use either '@WebMvcTest' or '@AutoConfigureMockMvc'
// @WebMvcTest - loads only controller and its dependencies
@AutoConfigureMockMvc // - loads full context
@Transactional
class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmployeeService employeeService;

    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        employees = List.of(
                employeeService.create(new Employee("First Test", 22)),
                employeeService.create(new Employee("Second Test", 33)),
                employeeService.create(new Employee("Third Test", 44))
        );
    }

    @Test
    void testFindAll() throws Exception {

        mvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employees"))
                .andExpect(content().string(containsString(employees.get(0).getName())))
                .andExpect(content().string(containsString(employees.get(1).getName())))
                .andExpect(content().string(containsString(employees.get(2).getName())));
    }

    @Test
    void testFindById() throws Exception {

        var employee = employees.get(1);

        mvc.perform(get("/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employee"))
                .andExpect(content().string(containsString(employee.getName())))
                .andExpect(content().string(containsString(String.valueOf(employee.getAge()))))
                .andExpect(content().string(containsString("Edit")));
    }

    @Test
    void testCreate() throws Exception {

        var newEmployee = new Employee("New Employee", 99);

        // POST
        mvc.perform(post("/employees")
                        .param("name", newEmployee.getName())
                        .param("age", String.valueOf(newEmployee.getAge())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/employees"));

        assertTrue(employeeService.findAll()
                .stream()
                .anyMatch(e -> e.getName().equals(newEmployee.getName())
                        && e.getAge().equals(newEmployee.getAge())));
    }

    @Test
    void testEditForm() throws Exception {

        var employee = employees.get(1);

        mvc.perform(get("/employees/{id}/edit", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/edit"))
                .andExpect(content().string(containsString(employee.getName())))
                .andExpect(content().string(containsString(String.valueOf(employee.getAge()))))
                .andExpect(content().string(containsString("Update")));
    }

    @Test
    void testUpdate() throws Exception {

        var employeePrototype = employees.get(1);
        var employeeUpdated = new Employee(employeePrototype.getId(), "updated name", 99);

        // PUT
        mvc.perform(put("/employees/{id}", employeePrototype.getId())
                        .param("name", employeeUpdated.getName())
                        .param("age", String.valueOf(employeeUpdated.getAge())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/employees/" + employeePrototype.getId()));

        assertEquals(employeeService.findById(employeePrototype.getId()), employeeUpdated);
    }

    @Test
    void testDeleteById() throws Exception {

        var employee = employees.get(1);

        assertNotNull(employeeService.findById(employee.getId()));

        // DELETE
        mvc.perform(delete("/employees/{id}", employee.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/employees"));

        assertNull(employeeService.findById(employee.getId()));
    }
}