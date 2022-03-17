package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EmployeesListController {
    private final EmployeeDAO employeeDAO;
    private final PositionHistoryDAO positionHistoryDAO;

    public EmployeesListController(EmployeeDAO employeeDAO, PositionHistoryDAO positionHistoryDAO) {
        this.employeeDAO = employeeDAO;
        this.positionHistoryDAO = positionHistoryDAO;
    }

    @GetMapping(value = "/employees")
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
                .map(TableEntry::new).collect(Collectors.toList());

        model.addAttribute("employees", entries);

        model.addAttribute("educationLevels", employeeDAO.getKnownEducationLevels());
        model.addAttribute("educationPlaces", employeeDAO.getKnownEducationPlaces());

        return "employees";
    }

    @Getter
    private class TableEntry {
        private final Long id;
        private final String name;
        private final String currentPosition;
        private final String education;

        public TableEntry(Employee emp) {
            id = emp.getId();
            name = emp.getName();
            education = emp.getEducationLevel() + ": " + emp.getEducationPlace();
            currentPosition = emp.getPositions().stream()
                    .filter(pos ->
                            pos.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                    .peek(positionHistoryDAO::initialize)
                    .map(PositionHistoryEntry::getPosition)
                    .map(Position::getName)
                    .findAny().orElse("Не работает");
        }
    }
}
