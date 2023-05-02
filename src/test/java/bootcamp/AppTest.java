package bootcamp;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class AppTest {
    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
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

    @Autowired
    MockMvc mockMvc;

    @Test
    public void givenActuatorHealthEndpoint_whenRequested_thenReturnsStatusUp() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void contextLoads() {
    }

}