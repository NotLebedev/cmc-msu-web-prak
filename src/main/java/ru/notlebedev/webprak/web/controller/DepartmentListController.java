package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DepartmentListController {
    private final DepartmentDAO departmentDAO;
    private final PositionDAO positionDAO;
    private final PositionHistoryDAO positionHistoryDAO;

    public DepartmentListController(DepartmentDAO departmentDAO, PositionDAO positionDAO, PositionHistoryDAO positionHistoryDAO) {
        this.departmentDAO = departmentDAO;
        this.positionDAO = positionDAO;
        this.positionHistoryDAO = positionHistoryDAO;
    }

    @GetMapping(value = "/departments")
    public String departments(
            @RequestParam(value = "hierarchy", required = false) Boolean hierarchy,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "chiefName", required = false) String chiefName,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        List<TableEntry> entries;
        if (Objects.equals(hierarchy, true)) {
            entries = departmentDAO.getHierarchy().stream()
                    .map(TableEntry::new)
                    .collect(Collectors.toList());
            model.addAttribute("displayMode", DisplayMode.HIERARCHY);
        } else {
            Department.Status stat = null;
            try {
                stat = Department.Status.forString(status);
            } catch (NullPointerException | IllegalArgumentException ignored) {}

            DepartmentDAO.Filter filter = DepartmentDAO.getFilterBuilder()
                    .name(name)
                    .status(stat)
                    .build();
            Map<Department, TableEntry> map = departmentDAO.getByFilter(filter).stream()
                    .map(department -> Map.entry(department, new TableEntry(department)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            map.forEach(((department, tableEntry) ->
                    tableEntry.setParent(map.get(department.getDepartmentSuper()))));

            if (chiefName != null)
                map.entrySet().removeIf(entry -> !entry.getValue().getHead().contains(chiefName));

            entries = map.values().stream()
                    .sorted(Comparator.comparing(TableEntry::getId)).collect(Collectors.toList());
            model.addAttribute("displayMode", DisplayMode.TABLE);
        }

        model.addAttribute("departments", entries);
        model.addAttribute("statuses", Arrays.stream(Department.Status.values())
                .map(Enum::toString).collect(Collectors.toList()));

        return "departments";
    }


    public enum DisplayMode {
        TABLE, HIERARCHY
    }

    @Getter
    private class TableEntry {
        private final Long id;
        private final String name;
        private final String status;
        private final String head;
        private final Long headId;
        private final List<TableEntry> children;
        @Setter
        private TableEntry parent;
        private final Long childCount;

        public TableEntry(Department dep) {
            id = dep.getId();
            name = dep.getName();

            status = dep.getStatus().toString();

            var headEmp = dep.getPositions().stream()
                    .filter(Position::isChief)
                    .peek(positionDAO::initialize)
                    .flatMap(position -> position.getPositionHistory().stream())
                    .filter(pos -> pos.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                    .peek(positionHistoryDAO::initialize)
                    .map(PositionHistoryEntry::getEmployee)
                    .findAny();
            head = headEmp.stream().map(Employee::getName).findAny().orElse("Отсутствует");
            headId = headEmp.stream().map(Employee::getId).findAny().orElse(null);

            children = dep.getChildren().stream()
                    .map(TableEntry::new)
                    .collect(Collectors.toList());
            childCount = children.stream()
                    .map(TableEntry::getChildCount)
                    .reduce((long) children.size(), Long::sum);
        }
    }
}
