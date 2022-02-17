package ru.notlebedev.webprak.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "employees")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Employee implements GenericEntity<Long> {
    @Id
    @Column(nullable = false, name = "employee_id")
    @GeneratedValue
    private Long id = null;

    @Column(nullable = false, name = "name")
    @NonNull
    private String name;

    @Column(nullable = false, name = "address")
    @NonNull
    private String address;

    @Column(nullable = false, name = "education_level")
    @NonNull
    private String educationLevel;

    @Column(nullable = false, name = "education_place")
    @NonNull
    private String educationPlace;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Employee employee = (Employee) o;
        return id != null && Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
