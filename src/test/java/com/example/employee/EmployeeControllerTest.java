package com.example.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    public void initEach() {
        employee = new Employee("test employee", 99);
        employee = employeeService.create(employee);
    }

    @Test
    void testFindAll() throws Exception {

        mvc.perform(get("/employees")
                    .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("employees/employees"));
    }

    @Test
    void testFindById() throws Exception {

        mvc.perform(get("/employees/{id}", employee.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("employees/employee"));
    }

    @Test
    void testCreate() throws Exception {

        var employeeForPosting = new Employee("created employee", 88);
        String json = new ObjectMapper().writeValueAsString(employeeForPosting);

        // POST
        mvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/employees"));
    }

    @Test
    void testEditForm() throws Exception {

        mvc.perform(get("/employees/{id}/edit", employee.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("employees/edit"));
    }

    @Test
    void testUpdate() throws Exception {

        employee.setName("updated name");
        employee.setAge(employee.getAge() * 2);
        String json = new ObjectMapper().writeValueAsString(employee);

        // PUT
        mvc.perform(put("/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/employees/" + employee.getId()));
    }

    @Test
    void testDeleteById() throws Exception {

        // DELETE
        mvc.perform(delete("/employees/{id}", employee.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/employees"));
    }
}