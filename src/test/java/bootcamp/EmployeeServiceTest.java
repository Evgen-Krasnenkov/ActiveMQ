package bootcamp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    private static List<Employee> employees;
    private static Employee employee;

    private static final Employee ONE_EMP = Employee.builder().name("Somename").build();
    private static final List<Employee> ONE_EMPS = List.of(ONE_EMP);
    private static final List<Employee> FIVE_EMPS =
            List.of(ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP);
    private static final List<Employee> TEN_EMPS =
            List.of(ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP, ONE_EMP);

    @InjectMocks
    private EmployeeService employeeService;
    @Mock
    private EmployeeRepository employeeRepository;

    @BeforeAll
    static void setUp() {
        employee = Employee.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Employee1")
                .role("ADMIN")
                .build();
        employees = List.of(employee);
    }


    @Test
    void givenEmployeeService_whenCallFindAllEmp_thenServiceReturnsSuccess() {
        Mockito.when(employeeRepository.findAll()).thenReturn(employees);
        List<Employee> employeeList = employeeService.findAll();
        Assertions.assertEquals(1L, employeeList.size());
        Assertions.assertEquals("ADMIN", employeeList.get(0).getRole());
        Assertions.assertEquals("Employee1", employeeList.get(0).getName());
    }

    @Test
    void givenEmployeeWithNoId_whenEmployeeSave_thenId45Returns() {
        Employee employeeWithId = Employee.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Employee1")
                .role("ADMIN")
                .id(45L)
                .build();
        Mockito.when(employeeRepository.save(employee)).thenReturn(employeeWithId);
        employeeService.save(employee);
        Assertions.assertEquals(45L, employeeWithId.getId());

    }

    @ParameterizedTest
    @MethodSource("employeeProvider")
    void givenEmployees_whenSaved_thenNumberOfRecordsMatched(List<Employee> emps) {
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
        for (Employee emp :
                emps) {
            employeeService.save(emp);
        }
        Mockito.verify(employeeRepository, Mockito.times(emps.size())).save(Mockito.any(Employee.class));
    }

    static Stream<List<Employee>> employeeProvider() {
        return Stream.of(
                ONE_EMPS,
                FIVE_EMPS,
                TEN_EMPS);
    }

}