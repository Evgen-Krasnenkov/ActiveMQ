package bootcamp;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeListener {
    private final EmployeeService employeeService;

    public EmployeeListener(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @JmsListener(destination = "${emp.jms.topic}", containerFactory = "empJmsContFactory")
    public void getEmployeeListener1(Employee emp) {
        log.info("Employee listener1: " + emp);
        employeeService.save(emp);
    }

    @JmsListener(destination = "${emp.jms.topic}", containerFactory = "empJmsContFactory")
    public void getEmployeeListener2(Employee emp) {
        log.info("Employee Listener2: " + emp);
        employeeService.save(emp);
    }
}