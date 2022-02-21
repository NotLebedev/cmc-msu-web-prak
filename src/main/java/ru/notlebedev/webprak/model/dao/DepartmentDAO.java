package ru.notlebedev.webprak.model.dao;

import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Department.Status;

import java.util.Collection;

public interface DepartmentDAO extends GenericDAO<Department, Long> {
    Collection<Department> getDepartmentByName(String name);
    Collection<Department> getDepartmentByNameByStatus(String name, Status status);
}
