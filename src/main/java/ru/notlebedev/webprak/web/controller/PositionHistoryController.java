package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;
import ru.notlebedev.webprak.util.FinalNonNullPair;

import java.util.Comparator;
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
        model.addAttribute("empId", employee.getId());
        model.addAttribute("list", employee.getPositions().stream()
                .peek(positionHistoryDAO::initialize)
                .map(PositionListEntry::new)
                .sorted()
                .collect(Collectors.toList()));

        model.addAttribute("openPositions", positionDAO.findAll().stream()
                .filter(pos -> pos.getPositionHistory().stream()
                        .noneMatch(e -> e.getStatus().equals(PositionHistoryEntry.Status.ACTIVE)))
                .filter(e -> e.getStatus().equals(Position.Status.ACTIVE))
                .map(e -> positionDAO.findById(e.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(e -> new FinalNonNullPair<>(e.getDepartment().getName() + " : " + e.getName(),
                        e.getId()))
                .collect(Collectors.toList()));

        return "history";
    }

    @PostMapping(value = "/positions/history")
    public String history(
            @RequestParam(value = "id") Long employeeId,
            @RequestParam(value = "mode") String mode,
            @RequestParam(value = "newPosition", required = false) Long newPosition,
            Model model) {
        Optional<Employee> emp = employeeDAO.findById(employeeId);

        if (emp.isEmpty())
            return "error";

        Employee employee = emp.get();
        // Fire from existing in any case
        if (mode.equals("FIRE") || mode.equals("ASSIGN")) {
            employee.getPositions().stream()
                    .filter(e -> e.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                    .peek(e -> e.setDateEnd(new java.sql.Date(System.currentTimeMillis())))
                    .peek(e -> e.setStatus(PositionHistoryEntry.Status.FINISHED))
                    .forEach(positionHistoryDAO::updateSave);
        }

        // In case of assignment assign to new
        if (mode.equals("ASSIGN")) {
            Optional<Position> pos = positionDAO.findById(newPosition);
            if (pos.isEmpty())
                return "error";

            Position position = pos.get();
            PositionHistoryEntry newEntry = new PositionHistoryEntry(position, employee,
                    PositionHistoryEntry.Status.ACTIVE, new java.sql.Date(System.currentTimeMillis()));

            positionHistoryDAO.save(newEntry);
        }

        return history(employeeId, model);
    }
    @Getter
    private class PositionListEntry implements Comparable<PositionListEntry> {
        private final Long departmentId;
        private final String departmentName;
        private final String name;
        private final Long id;
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
            id = entry.getId();
        }

        @Override
        public int compareTo(@NonNull PositionListEntry o) {
            return Comparator.comparing(PositionListEntry::getDate).
                    thenComparing(PositionListEntry::getId).compare(this, o);
        }
    }
}
