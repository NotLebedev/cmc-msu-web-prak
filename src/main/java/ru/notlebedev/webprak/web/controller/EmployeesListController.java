package ru.notlebedev.webprak.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.entity.Employee;

import java.util.Collection;

@Controller
public class EmployeesListController {
    private final EmployeeDAO employeeDAO;

    public EmployeesListController(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public String employees(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "educationLevel", required = false) String educationLevel,
            @RequestParam(value = "educationPlace", required = false) String educationPlace,
            Model model) {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .name(name)
                .address(address)
                .educationLevel(educationLevel)
                .educationPlace(educationPlace)
                .build();

        Collection<Employee> employeesDisplayed = employeeDAO.getByFilter(filter);
        model.addAttribute("employees", employeesDisplayed);

        return "employees";
    }
}
