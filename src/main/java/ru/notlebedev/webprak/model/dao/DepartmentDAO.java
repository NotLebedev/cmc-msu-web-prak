package ru.notlebedev.webprak.model.dao;

import lombok.Data;
import ru.notlebedev.webprak.model.entity.Department;

import java.util.Collection;

public interface DepartmentDAO extends GenericDAO<Department, Long> {
    Collection<Department> getByFilter(Filter filter);

    @Data
    class Filter {
        private String name;
        private Department.Status status;
    }
}
