package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class EmployeesListController {
    private final EmployeeDAO employeeDAO;
    private final PositionHistoryDAO positionHistoryDAO;
    private final DepartmentDAO departmentDAO;
    private final PositionDAO positionDAO;

    public EmployeesListController(EmployeeDAO employeeDAO, PositionHistoryDAO positionHistoryDAO,
                                   DepartmentDAO departmentDAO, PositionDAO positionDAO) {
        this.employeeDAO = employeeDAO;
        this.positionHistoryDAO = positionHistoryDAO;
        this.departmentDAO = departmentDAO;
        this.positionDAO = positionDAO;
    }

    @GetMapping(value = "/employees")
    public String employees(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "educationLevel", required = false) String educationLevel,
            @RequestParam(value = "educationPlace", required = false) String educationPlace,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .name(name)
                .address(address)
                .educationLevel(educationLevel)
                .educationPlace(educationPlace)
                .build();

        Collection<Employee> employeesDisplayed = employeeDAO.getByFilter(filter);
        if (!department.isEmpty())
            employeesDisplayed.removeIf(employee -> employee.getPositions().stream()
                    .peek(positionHistoryDAO::initialize)
                    .filter(pos -> pos.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                    .map(PositionHistoryEntry::getPosition)
                    .map(position -> positionDAO.findById(position.getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(positionDAO::initialize)
                    .map(Position::getDepartment)
                    .map(Department::getName)
                    .noneMatch(str -> str.equals(department)));
        if (!status.isEmpty()) {
            PositionHistoryEntry.Status expected = status.equals("Работает") ? PositionHistoryEntry.Status.ACTIVE :
                    PositionHistoryEntry.Status.FINISHED;
            employeesDisplayed.removeIf(employee -> {
                if (expected == PositionHistoryEntry.Status.FINISHED && employee.getPositions().size() == 0)
                    return false;
                return employee.getPositions().stream()
                        .peek(positionHistoryDAO::initialize)
                        .noneMatch(pos -> pos.getStatus().equals(expected));
            });
        }
        List<TableEntry> entries = employeesDisplayed.parallelStream()
                .map(TableEntry::new).collect(Collectors.toList());

        model.addAttribute("employees", entries);

        model.addAttribute("educationLevels", employeeDAO.getKnownEducationLevels());
        model.addAttribute("educationPlaces", employeeDAO.getKnownEducationPlaces());
        model.addAttribute("departments", departmentDAO.findAll().stream()
                .map(Department::getName)
                .collect(Collectors.toList()));
        model.addAttribute("statuses", Arrays.asList("Работает", "Не работает"));

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
