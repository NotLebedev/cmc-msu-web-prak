package ru.notlebedev.webprak.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "positions")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Position implements GenericEntity<Long> {
    @Id
    @Column(nullable = false, name = "position_id")
    @GeneratedValue
    private Long id = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "dep_id")
    @NonNull
    private Department department;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "position")
    @ToString.Exclude
    private Set<PositionHistoryEntry> positionHistory;

    @Column(nullable = false, name = "is_chief")
    private boolean isChief;

    @Column(nullable = false, name = "position_name")
    @NonNull
    private String name;

    @Column(nullable = false, name = "position_desc")
    @NonNull
    private String description;

    @Column(nullable = false, name = "status")
    @Enumerated
    @NonNull
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return isChief == position.isChief && Objects.equals(id, position.id) && name.equals(position.name)
                && description.equals(position.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isChief, name, description);
    }

    public enum Status {
        ACTIVE,
        CLOSED
    }
}
