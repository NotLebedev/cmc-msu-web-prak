package ru.notlebedev.webprak.model.dao;

import lombok.Builder;
import lombok.Getter;
import ru.notlebedev.webprak.model.entity.Employee;

import java.util.Collection;

public interface EmployeeDAO extends GenericDAO<Employee, Long> {
    Collection<Employee> getByFilter(Filter filter);
    Collection<String> getKnownEducationLevels();
    Collection<String> getKnownEducationPlaces();

    @Builder
    @Getter
    class Filter {
        private String name;
        private String address;
        private String educationLevel;
        private String educationPlace;
    }

    static Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}
