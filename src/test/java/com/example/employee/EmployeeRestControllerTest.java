package com.example.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class EmployeeRestControllerTest {

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

        mvc.perform(get("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is(employee.getName())))
                .andExpect(jsonPath("$[0].age", is(employee.getAge())));
    }

    @Test
    void testFindById() throws Exception {

        mvc.perform(get("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(employee.getName())))
                .andExpect(jsonPath("$.age", is(employee.getAge())));

        mvc.perform(get("/api/employees/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {

        var employeeForPosting = new Employee("created employee", 88);
        String json = new ObjectMapper().writeValueAsString(employeeForPosting);

        // POST
        mvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
//                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(employeeForPosting.getName())))
                .andExpect(jsonPath("$.age", is(employeeForPosting.getAge())));

        // GET
        mvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[1].name", is(employeeForPosting.getName())))
                .andExpect(jsonPath("$[1].age", is(employeeForPosting.getAge())));
    }

    @Test
    void testUpdate() throws Exception {

        employee.setName("updated name");
        employee.setAge(employee.getAge() * 2);
        String json = new ObjectMapper().writeValueAsString(employee);

        // PUT
        mvc.perform(put("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
//                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(employee.getName())))
                .andExpect(jsonPath("$.age", is(employee.getAge())));

        // GET
        mvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is(employee.getName())))
                .andExpect(jsonPath("$[0].age", is(employee.getAge())));
    }

    @Test
    void testDeleteById() throws Exception {

        // GET
        mvc.perform(get("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // DELETE
        mvc.perform(delete("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // GET
        mvc.perform(get("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}