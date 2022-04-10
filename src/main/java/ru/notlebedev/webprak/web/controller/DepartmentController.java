package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.entity.Department;

import java.util.Optional;

@Controller
public class DepartmentController {
    private final DepartmentDAO departmentDAO;

    public DepartmentController(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @GetMapping(value = "/departments/department")
    public String department(
            @RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id == null) {
            model.addAttribute("mode", Mode.CREATE);

            return "department";
        }

        model.addAttribute("mode", Mode.UPDATE);

        Optional<Department> dep = departmentDAO.findById(id);

        if (dep.isEmpty())
            return "redirect:/error";

        model.addAttribute("department", new DepartmentEntry(dep.get()));

        return "department";
    }

    public enum Mode {UPDATE, CREATE}

    @Getter
    private static class DepartmentEntry {
        private final Long id;
        private final String name;
        private final String superName;
        private final Long superId;
        private final Department.Status status;

        private DepartmentEntry(Department department) {
            id = department.getId();
            name = department.getName();
            if (department.getDepartmentSuper() != null) {
                superName = department.getDepartmentSuper().getName();
                superId = department.getDepartmentSuper().getId();
            } else {
                superName = null;
                superId = null;
            }
            status = department.getStatus();
        }
    }
}
