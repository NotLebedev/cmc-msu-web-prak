package ru.notlebedev.webprak.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Department implements GenericEntity<Long> {
    @Id
    @Column(nullable = false, name = "department_id")
    @GeneratedValue
    private Long id = null;

    @Column(nullable = false, name = "name")
    @NonNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_super")
    @ToString.Exclude
    private Department departmentSuper;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "departmentSuper")
    @ToString.Exclude
    private Set<Department> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
    @ToString.Exclude
    private Set<Position> positions;

    @Column(nullable = false, name = "status")
    @Enumerated
    @NonNull
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public enum Status {
        ACTIVE("Функционирует"),
        DEFUNCT("Закрыто");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
