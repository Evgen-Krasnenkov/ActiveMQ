package bootcamp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {
    private Employee employee;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Container
    static MySQLContainer<?> database = new MySQLContainer<>("mysql:8")
            .withDatabaseName("bootcamp")
            .withUsername("bootcamp")
            .withPassword("bootcamp");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
        propertyRegistry.add("spring.datasource.password", database::getPassword);
        propertyRegistry.add("spring.datasource.username", database::getUsername);
    }

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Employee1")
                .role("ADMIN")
                .build();
    }

    @Test
    @Sql("/insertEmployees.sql")
    void given5EmployeesInDb_whenFindAll_thenReturnsCorrectSize() {
        List<Employee> all = employeeRepository.findAll();
        Assertions.assertEquals(5, all.size());
    }

    @Test
    @Sql("/insertEmployees.sql")
    void given5EmpsInDb_whenAddEmployee_thenReturnsCorrectData() {
        Employee saveEmployee = employeeRepository.save(employee);
        int size = employeeRepository.findAll().size();
        Assertions.assertEquals(6, size);
        Assertions.assertEquals("ADMIN", saveEmployee.getRole());
        Assertions.assertEquals(6, saveEmployee.getId());
    }
}
