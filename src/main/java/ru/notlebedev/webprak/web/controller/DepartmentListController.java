package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.entity.Department;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DepartmentListController {
    private final DepartmentDAO departmentDAO;

    public DepartmentListController(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @GetMapping(value = "/departments")
    public String departments(Model model) {
        List<TableEntry> entries = departmentDAO.getHierarchy().stream()
                .map(TableEntry::new)
                .collect(Collectors.toList());

        model.addAttribute("departments", entries);

        return "departments";
    }

    @Getter
    private static class TableEntry {
        private final Long id;
        public final String name;
        public final List<TableEntry> children;

        public TableEntry(Department dep) {
            id = dep.getId();
            name = dep.getName();
            children = dep.getChildren().stream().map(TableEntry::new).collect(Collectors.toList());
        }
    }
}
