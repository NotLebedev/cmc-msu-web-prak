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
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.sql.Date;
import java.util.*;
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

    @PostMapping(value = "/positions/position")
    public String position(
            @RequestParam(value = "mode") String mode,
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "depId") Long depId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status") String status,
            Model model) {
        description = Objects.requireNonNullElse(description,"");

        Long departmentId = -1L;
        if (mode.equals("UPDATE")) {
            Optional<Position> pos = positionDAO.findById(id);
            if (pos.isEmpty())
                return "error";

            Position position = pos.get();
            departmentId = position.getDepartment().getId();
            position.setName(name);
            position.setDescription(description);
            position.setStatus(status.equals("true") ? Position.Status.ACTIVE : Position.Status.CLOSED);

            if (position.getStatus().equals(Position.Status.CLOSED))
                position.getPositionHistory().stream()
                        .filter(e -> e.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                        .peek(e -> e.setDateEnd(new Date(System.currentTimeMillis())))
                        .peek(e -> e.setStatus(PositionHistoryEntry.Status.FINISHED))
                        .forEach(positionHistoryDAO::updateSave);

            positionDAO.updateSave(position);
        } else if (mode.equals("CREATE")) {
            if (name.isBlank())
                return "error";
            departmentId = depId;
            Optional<Department> dep = departmentDAO.findById(depId);
            if (dep.isEmpty())
                return "error";
            Position position = new Position(dep.get(), name, description,
                    status.equals("true") ? Position.Status.ACTIVE : Position.Status.CLOSED);

            positionDAO.save(position);
        }

        return "redirect:/departments/department?id=" + departmentId;
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
            if (superName != null && !superName.isEmpty()) {
                Collection<Department> superDep = departmentDAO.getByFilter(DepartmentDAO.Filter.builder()
                        .name(superName)
                        .build());
                if (superDep.size() == 1)
                    department.setDepartmentSuper(superDep.stream().findAny().get());
            } else
                department.setDepartmentSuper(null);

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

            Position chiefPosition = new Position(department, "Начальник", "Начальник подразделения",
                    Position.Status.ACTIVE);
            chiefPosition.setChief(true);
            positionDAO.save(chiefPosition);

            newId = department.getId();
        } else if (mode.equals("DELETE")) {
            Optional<Department> dep = departmentDAO.findById(id);
            if (dep.isEmpty())
                return "error";

            Department department = dep.get();
            departmentDAO.initialize(department);

            // Close all positions
            department.getPositions().stream()
                    .peek(position -> position.setStatus(Position.Status.CLOSED))
                    .peek(positionDAO::updateSave)
                    .peek(positionDAO::initialize)
                    .flatMap(position -> position.getPositionHistory().stream())
                    .filter(e -> e.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                    .peek(e -> e.setStatus(PositionHistoryEntry.Status.FINISHED))
                    .peek(e -> e.setDateEnd(new Date(System.currentTimeMillis())))
                    .forEach(positionHistoryDAO::updateSave);

            department.setStatus(Department.Status.DEFUNCT);
            departmentDAO.updateSave(department);

            newId = department.getId();
        } else if (mode.equals("RESTORE")) {
            Optional<Department> dep = departmentDAO.findById(id);
            if (dep.isEmpty())
                return "error";

            Department department = dep.get();
            department.setStatus(Department.Status.ACTIVE);

            departmentDAO.updateSave(department);
            department.getPositions().stream()
                    .filter(Position::isChief)
                    .peek(position -> position.setStatus(Position.Status.ACTIVE))
                    .forEach(positionDAO::updateSave);
            newId = department.getId();
        }

        return setupModel(departmentDAO.findById(newId), model);
    }

    private String setupModel(Optional<Department> dep, Model model) {
        if (dep.isEmpty())
            return "redirect:/departments";

        Collection<String> departmentList = departmentDAO.findAll().stream()
                .map(Department::getName)
                .collect(Collectors.toList());
        model.addAttribute("departmentsList", departmentList);

        model.addAttribute("department", new DepartmentEntry(dep.get()));
        model.addAttribute("mode", Mode.UPDATE);
        return "department";
    }

    public enum Mode {UPDATE, CREATE}

    @Getter
    private class DepartmentEntry {
        private final Long id;
        private final String name;
        private final String superName;
        private final Long superId;
        private final Department.Status status;

        private final List<PositionEntry> positions;

        private DepartmentEntry() {
            id = null;
            name = null;
            superName = null;
            superId = null;
            status = null;
            positions = Collections.emptyList();
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

            positions = department.getPositions().stream()
                    .map(position -> positionDAO.findById(position.getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(positionDAO::initialize)
                    .map(pos -> {
                        Employee emp = pos.getPositionHistory().stream()
                                .filter(e -> e.getStatus().equals(PositionHistoryEntry.Status.ACTIVE))
                                .map(e -> positionHistoryDAO.findById(e.getId()))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .map(PositionHistoryEntry::getEmployee)
                                .findAny().orElse(null);

                        return new PositionEntry(pos, emp);
                    })
                    .collect(Collectors.toList());
        }

        @Getter
        class PositionEntry {
            private final Long id;
            private final String name;
            private final String employee;
            private final Long employeeId;
            private final String status;
            private final String description;

            PositionEntry(Position pos, Employee emp) {
                this.id = pos.getId();
                this.name = pos.getName();
                if (emp != null) {
                    this.employee = emp.getName();
                    this.employeeId = emp.getId();
                } else {
                    this.employee = "";
                    this.employeeId = -1L;
                }
                this.description = pos.getDescription();
                this.status = pos.getStatus().equals(Position.Status.ACTIVE) ? "true" : "false";
            }
        }
    }
}
