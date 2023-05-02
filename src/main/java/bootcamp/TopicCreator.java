package bootcamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

@Component
public class TopicCreator implements CommandLineRunner {
    @Value("${emp.jms.topic}")
    private String topic;
    @Autowired
    ConnectionFactory connectionFactory;

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.createSession().createTopic(topic);
        }
    }
}
