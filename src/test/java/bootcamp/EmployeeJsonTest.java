package bootcamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
@Nested
public class EmployeeJsonTest {

    @Autowired
    private JacksonTester<Employee> json;
    private Employee employee;

    @BeforeEach
    void setup() {
        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .uuid("abc123")
                .role("Manager")
                .build();
    }

    @Test
    void givenEmployee_whenSerialize_thenJsonCorrect() throws Exception {
        JsonContent<Employee> result = this.json.write(employee);
        assertAll(
                "employee",
                () -> assertThat(result).hasJsonPathStringValue("$.name"),
                () -> assertNotNull(result),
                () -> assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe"),
                () -> assertThat(result).doesNotHaveJsonPath("$.dob"),
                () -> assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1),
                () -> assertThat(result).extractingJsonPathStringValue("$.uuid")
                        .isNotBlank().isEqualTo("abc123")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"John Doe\", \"role\":\"Manager\", \"uuid\":\"abc123\", \"id\": \"1\"}",
            "{\"id\": \"1\", \"name\":\"John Doe\"}",
            "{\"name\":\"John Doe\", \"role\":\"Manager\", \"id\": \"1\"}",
            "{\"uuid\":\"abc123\", \"id\": \"1\", \"name\":\"John Doe\"}"
    })
    void giveJson_whenDeserialize_thenEmployeeCorrect(String jsonEmployee) throws IOException {
        Employee employeeParsed = this.json.parse(jsonEmployee).getObject();
        assertInstanceOf(Employee.class, employee);
        assertThat(employeeParsed.getId()).isEqualTo(employee.getId());
        assertThat(employeeParsed.getName()).isEqualTo(employee.getName());
    }

}
