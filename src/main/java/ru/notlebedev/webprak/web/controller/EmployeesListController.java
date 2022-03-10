package ru.notlebedev.webprak.web.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        List<TableEntry> entries = employeesDisplayed.parallelStream()
                .map(emp -> new TableEntry(emp.getName(), emp.getAddress(),
                        emp.getPositions().stream().filter(pos ->
                                        pos.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                                .map(PositionHistoryEntry::getPosition)
                                .map(Position::getName)
                                .findAny().orElse("Не работает")))
                .collect(Collectors.toList());
        model.addAttribute("employees", entries);

        return "employees";
    }

    @Getter
    @AllArgsConstructor
    private static class TableEntry {
        private final String name;
        private final String address;
        private final String currentPosition;
    }
}
