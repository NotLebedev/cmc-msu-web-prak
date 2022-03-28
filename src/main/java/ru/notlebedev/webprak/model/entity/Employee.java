package ru.notlebedev.webprak.model.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<PositionHistoryEntry> positions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && name.equals(employee.name) && address.equals(employee.address) && educationLevel.equals(employee.educationLevel) && educationPlace.equals(employee.educationPlace);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
