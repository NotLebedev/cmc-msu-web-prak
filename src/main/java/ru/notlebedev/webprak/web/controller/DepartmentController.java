package ru.notlebedev.webprak.web.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.util.Collection;
import java.sql.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DepartmentController {
    private final DepartmentDAO departmentDAO;
    private final PositionDAO positionDAO;
    private final PositionHistoryDAO positionHistoryDAO;

    public DepartmentController(DepartmentDAO departmentDAO, PositionDAO positionDAO,
                                PositionHistoryDAO positionHistoryDAO) {
        this.departmentDAO = departmentDAO;
        this.positionDAO = positionDAO;
        this.positionHistoryDAO = positionHistoryDAO;
    }

    @GetMapping(value = "/departments/department")
    public String department(
            @RequestParam(value = "id", required = false) Long id,
            Model model) {
        Collection<String> departmentList = departmentDAO.findAll().stream()
                .map(Department::getName)
                .collect(Collectors.toList());
        model.addAttribute("departmentsList", departmentList);

        if (id == null) {
            model.addAttribute("mode", Mode.CREATE);
            model.addAttribute("department", new DepartmentEntry());
            return "department";
        }

        model.addAttribute("mode", Mode.UPDATE);

        Optional<Department> dep = departmentDAO.findById(id);

        if (dep.isEmpty())
            return "redirect:/error";

        model.addAttribute("department", new DepartmentEntry(dep.get()));

        return "department";
    }

    @PostMapping(value = "/departments/department")
    public String department(
            @RequestParam(value = "mode") String mode,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "superName", required = false) String superName,
            Model model) {

        Long newId = 0L;

        if(mode.equals("UPDATE")) {
            Optional<Department> dep = departmentDAO.findById(id);
            if (dep.isEmpty())
                return "error";

            Department department = dep.get();
            if (name != null)
                department.setName(name);
            if (superName != null) {
                Collection<Department> superDep = departmentDAO.getByFilter(DepartmentDAO.Filter.builder()
                        .name(superName)
                        .build());
                if (superDep.size() == 1)
                    department.setDepartmentSuper(superDep.stream().findAny().get());
            }

            departmentDAO.updateSave(department);

            newId = department.getId();
        } else if (mode.equals("CREATE")) {
            Department department = new Department(name, Department.Status.ACTIVE);

            if (superName != null) {
                Collection<Department> superDep = departmentDAO.getByFilter(DepartmentDAO.Filter.builder()
                        .name(superName)
                        .build());
                if (superDep.size() == 1)
                    department.setDepartmentSuper(superDep.stream().findAny().get());
            }

            departmentDAO.save(department);
            newId = department.getId();
        } else if (mode.equals("DELETE")) {
            Optional<Department> dep = departmentDAO.findById(id);
            if (dep.isEmpty())
                return "error";

            Department department = dep.get();

            // Close all positions
            department.getPositions().stream()
                    .peek(position -> position.setStatus(Position.Status.CLOSED))
                    .peek(positionDAO::save)
                    .flatMap(position -> position.getPositionHistory().stream())
                    .filter(e -> e.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                    .peek(e -> e.setStatus(PositionHistoryEntry.Status.FINISHED))
                    .peek(e -> e.setDateEnd(new Date(System.currentTimeMillis())))
                    .forEach(positionHistoryDAO::save);

            department.setStatus(Department.Status.DEFUNCT);
            departmentDAO.updateSave(department);

            return "redirect:/departments";
        }

        return "redirect:/departments";
    }

    public enum Mode {UPDATE, CREATE}

    @Getter
    private static class DepartmentEntry {
        private final Long id;
        private final String name;
        private final String superName;
        private final Long superId;
        private final Department.Status status;

        private DepartmentEntry() {
            id = null;
            name = null;
            superName = null;
            superId = null;
            status = null;
        }

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
