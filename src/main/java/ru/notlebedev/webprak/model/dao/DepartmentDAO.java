package ru.notlebedev.webprak.model.dao;

import lombok.Builder;
import lombok.Getter;
import ru.notlebedev.webprak.model.entity.Department;

import java.util.Collection;

public interface DepartmentDAO extends GenericDAO<Department, Long> {
    Collection<Department> getByFilter(Filter filter);

    /**
     * @return Set of all top level departments with
     * children initialized to the bottom of hierarchy
     */
    Collection<Department> getHierarchy();

    @Builder
    @Getter
    class Filter {
        private String name;
        private Department.Status status;
    }

    static Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}
