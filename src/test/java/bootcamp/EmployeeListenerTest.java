package bootcamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static bootcamp.EmployeePublisherTest.TIMEOUT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@AutoConfigureMockMvc
@SpringBootTest
@Import({JMSConfig.class, TestConfig.class})
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class EmployeeListenerTest {
    private static final Logger logger = LoggerFactory.getLogger(EmployeePublisherTest.class.getName());
    private static final String ACTIVEMQ_IMAGE = "rmohr/activemq:latest";
    private static final int ACTIVEMQ_PORT = 61616;
    private static final String TCP_FORMAT = "tcp://%s:%d";

    @Container
    static GenericContainer<?> activemq = new GenericContainer<>(ACTIVEMQ_IMAGE).withExposedPorts(ACTIVEMQ_PORT);

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
        final String url = String.format(TCP_FORMAT, activemq.getHost(), activemq.getFirstMappedPort());
        logger.info("ActiveMQ-URL: '{}'", url);
        propertyRegistry.add("activemq.broker-url", () -> url);
    }

    private Employee employee;

    @Autowired
    private EmployeeListener employeeListener;

    @Autowired
    private Publisher<Employee> employeePublisher;
    @MockBean
    private EmployeeService service;

    @BeforeEach
    void setUp() {
        this.employee = Employee.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Employee1")
                .role("ADMIN")
                .build();
    }

    @Test
    public void givenEmployee_whenPublisherSends_thenListenersGetEmployee() {
        Mockito.when(service.save(employee)).thenReturn(employee);
        employeePublisher.send(employee);
        await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(
                () -> verify(service, atLeast(2)).save(any(Employee.class)));
        verify(service, atLeast(2)).save(any());
    }

}