package bootcamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

@TestConfiguration
public class TestConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class.getName());
    @Value("${emp.jms.topic}")
    private String topic;
    @Autowired
    JmsTemplate jmsTemplate;

    @Bean
    public Publisher<Employee> employeePublisher() {
        return employee -> {
            Employee emp = Employee
                    .builder()
                    .uuid(UUID.randomUUID().toString())
                    .name(employee.getName())
                    .role(employee.getRole()).build();
            logger.info("Sending Employee Object: " + emp);
            jmsTemplate.convertAndSend(topic, emp);
            return emp;
        };
    }
}
