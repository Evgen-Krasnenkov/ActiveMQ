package bootcamp;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee save(Employee emp) {
        return employeeRepository.save(emp);
    }

    public Employee update(Long id, Employee emp) {
        Employee employee = this.getById(id);
        employee.setRole(emp.getRole());
        employee.setName(emp.getName());
        employee.setUuid(employee.getUuid());
        return employeeRepository.save(employee);
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Can't find employee by id:" + id));
    }

    public void delete(Employee employee) {
        employeeRepository.delete(employee);
    }
}
