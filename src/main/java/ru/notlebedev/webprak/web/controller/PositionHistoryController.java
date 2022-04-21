package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PositionHistoryController {
    private final EmployeeDAO employeeDAO;
    private final PositionDAO positionDAO;
    private final PositionHistoryDAO positionHistoryDAO;

    public PositionHistoryController(EmployeeDAO employeeDAO, PositionDAO positionDAO, PositionHistoryDAO positionHistoryDAO) {
        this.employeeDAO = employeeDAO;
        this.positionDAO = positionDAO;
        this.positionHistoryDAO = positionHistoryDAO;
    }

    @GetMapping(value = "/positions/history")
    public String history(
            @RequestParam(value = "id") Long employeeId,
            Model model) {
        Optional<Employee> emp = employeeDAO.findById(employeeId);

        if (emp.isEmpty())
            return "error";

        Employee employee = emp.get();
        model.addAttribute("empName", employee.getName());
        model.addAttribute("list", employee.getPositions().stream()
                .peek(positionHistoryDAO::initialize)
                .map(PositionListEntry::new)
                .sorted()
                .collect(Collectors.toList()));

        return "history";
    }

    @Getter
    private class PositionListEntry implements Comparable<PositionListEntry> {
        private final Long departmentId;
        private final String departmentName;
        private final String name;
        private final String dateStart;
        private final String dateEnd;
        private final boolean isCurrent;
        private final Date date;

        private PositionListEntry(PositionHistoryEntry entry) {
            Department dep = positionDAO.findById(entry.getPosition().getId()).get().getDepartment();
            departmentId = dep.getId();
            departmentName = dep.getName();
            name = entry.getPosition().getName();
            dateStart = entry.getDateStart().toLocalDate().toString();
            if (entry.getDateEnd() != null) {
                dateEnd = entry.getDateEnd().toLocalDate().toString();
                isCurrent = false;
            } else {
                dateEnd = "Текущая должность";
                isCurrent = true;
            }
            date = entry.getDateStart();
        }

        @Override
        public int compareTo(PositionListEntry o) {
            return date.compareTo(o.date);
        }
    }
}
