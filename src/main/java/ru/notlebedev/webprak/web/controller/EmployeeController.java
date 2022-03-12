package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class EmployeeController {
    private final EmployeeDAO employeeDAO;
    private final PositionHistoryDAO positionHistoryDAO;

    public EmployeeController(EmployeeDAO employeeDAO, PositionHistoryDAO positionHistoryDAO) {
        this.employeeDAO = employeeDAO;
        this.positionHistoryDAO = positionHistoryDAO;
    }

    @GetMapping(value = "/employees/employee")
    public String employee(
            @RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id == null) {
            model.addAttribute("employee", new EmployeeEntry());
            return "employee";
        }

        Optional<Employee> emp = employeeDAO.findById(id);
        if (emp.isEmpty())
            return "index";
        emp.get().getPositions().forEach(positionHistoryDAO::initialize);

        model.addAttribute("employee", new EmployeeEntry(emp.get()));

        return "employee";
    }

    @Getter
    private static class EmployeeEntry {
        private final Mode actionMode;
        private final String name;
        private final String address;
        private final String educationLevel;
        private final String educationPlace;
        private final Long employmentDuration;
        private final List<String> employmentHistory;

        private EmployeeEntry() {
            actionMode = Mode.CREATE;
            name = null;
            address = null;
            educationLevel = null;
            educationPlace = null;
            employmentDuration = 0L;
            employmentHistory = null;
        }

        private EmployeeEntry(Employee employee) {
            actionMode = Mode.UPDATE;
            name = employee.getName();
            address = employee.getAddress();
            educationLevel = employee.getEducationLevel();
            educationPlace = employee.getEducationPlace();
            employmentDuration = employee.getPositions().stream()
                    .map(pos -> {
                        if (pos.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                            return TimeUnit.DAYS.convert(Math.abs(new Date().getTime() - pos.getDateStart().getTime()),
                                    TimeUnit.MILLISECONDS);
                        else
                            return TimeUnit.DAYS.convert(Math.abs(pos.getDateEnd().getTime() - pos.getDateStart().getTime()),
                                    TimeUnit.MILLISECONDS);
                    })
                    .reduce(0L, Long::sum);
            employmentHistory = employee.getPositions().stream()
                    .map(pos -> pos.getPosition().getName())
                    .collect(Collectors.toList());
        }

        enum Mode {UPDATE, CREATE};
    }
}
