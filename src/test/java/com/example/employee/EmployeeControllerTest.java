package com.example.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
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
        employee = employeeService.create(new Employee("test employee", 99));
    }

    @Test
    void testFindAll() throws Exception {

        mvc.perform(get("/employees")
                    .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("employees/employees"))
                .andExpect(content().string(containsString("test employee")));
    }

    @Test
    void testFindById() throws Exception {

        mvc.perform(get("/employees/{id}", employee.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("employees/employee"))
                .andExpect(content().string(containsString("test employee")));
    }

    @Test
    void testCreate() throws Exception {

        var employeeForPosting = new Employee("created employee", 88);

        // POST
        mvc.perform(post("/employees")
                        .param("name", employeeForPosting.getName())
                        .param("age", String.valueOf(employeeForPosting.getAge())))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/employees"));

        assertTrue(employeeService.findAll()
                .stream()
                .anyMatch(e -> e.getName().equals(employeeForPosting.getName())
                        && e.getAge().equals(employeeForPosting.getAge())));
    }

    @Test
    void testEditForm() throws Exception {

        mvc.perform(get("/employees/{id}/edit", employee.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("employees/edit"))
                .andExpect(content().string(containsString("test employee")));
    }

    @Test
    void testUpdate() throws Exception {

        employee.setName("updated name");
        employee.setAge(employee.getAge() * 2);
//        String json = new ObjectMapper().writeValueAsString(employee);

        // PUT
        mvc.perform(put("/employees/{id}", employee.getId())
                        .param("name", employee.getName())
                        .param("age", String.valueOf(employee.getAge())))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/employees/" + employee.getId()));

        var employeeFound = employeeService.findById(employee.getId());
        assertEquals(employee.getName(), employeeFound.getName());
        assertEquals(employee.getAge(), employeeFound.getAge());
    }

    @Test
    void testDeleteById() throws Exception {

        assertNotNull(employeeService.findById(employee.getId()));

        // DELETE
        mvc.perform(delete("/employees/{id}", employee.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/employees"));

        assertNull(employeeService.findById(employee.getId()));
    }
}