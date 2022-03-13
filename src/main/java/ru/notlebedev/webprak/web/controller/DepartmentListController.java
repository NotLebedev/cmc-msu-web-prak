package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.entity.Department;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class DepartmentListController {
    private final DepartmentDAO departmentDAO;

    public DepartmentListController(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @GetMapping(value = "/departments")
    public String departments(
            @RequestParam(value = "hierarchy", required = false) Boolean hierarchy,
            Model model) {
        List<TableEntry> entries;
        if (Objects.equals(hierarchy, true)) {
            entries = departmentDAO.getHierarchy().stream()
                    .map(TableEntry::new)
                    .collect(Collectors.toList());
            model.addAttribute("displayMode", DisplayMode.HIERARCHY);
        } else {
            Map<Department, TableEntry> map = departmentDAO.findAll().stream()
                    .map(department -> Map.entry(department, new TableEntry(department)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            map.forEach(((department, tableEntry) ->
                    tableEntry.setParent(map.get(department.getDepartmentSuper()))));

            entries = map.values().stream()
                    .sorted(Comparator.comparing(TableEntry::getId)).collect(Collectors.toList());
            model.addAttribute("displayMode", DisplayMode.TABLE);
        }

        model.addAttribute("departments", entries);

        return "departments";
    }


    public enum DisplayMode {
        TABLE, HIERARCHY
    }

    @Getter
    private static class TableEntry {
        private final Long id;
        private final String name;
        private final String status;
        private final List<TableEntry> children;
        @Setter
        private TableEntry parent;
        private final Long childCount;

        public TableEntry(Department dep) {
            id = dep.getId();
            name = dep.getName();

            status = switch(dep.getStatus()) {
                case ACTIVE -> "Функционирует";
                case DEFUNCT -> "Закрыто";
            };

            children = dep.getChildren().stream()
                    .map(TableEntry::new)
                    .collect(Collectors.toList());
            childCount = children.stream()
                    .map(TableEntry::getChildCount)
                    .reduce((long) children.size(), Long::sum);
        }
    }
}
