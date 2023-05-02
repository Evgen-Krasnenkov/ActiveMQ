package bootcamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {
    private Employee employee;
    private final List<Employee> employees = new ArrayList<>();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        this.employee = Employee.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Employee1")
                .role("ADMIN")
                .id(1L)
                .build();

        this.employees.add(employee);
    }

    @Test
    void whenUsersInDb_thenGetHttpFindAllSuccess() throws Exception {
        Mockito.when(employeeService.findAll()).thenReturn(employees);
        mockMvc.perform(MockMvcRequestBuilders.get("/employee"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
