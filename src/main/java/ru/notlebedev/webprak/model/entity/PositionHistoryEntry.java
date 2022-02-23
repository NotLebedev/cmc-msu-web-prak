package ru.notlebedev.webprak.model.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "position_history")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class PositionHistoryEntry implements GenericEntity<Long> {
    @Id
    @Column(nullable = false, name = "history_id")
    @GeneratedValue
    private Long id = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    @ToString.Exclude
    @NonNull
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "emp_id")
    @ToString.Exclude
    @NonNull
    private Employee employee;

    @Column(nullable = false, name = "status")
    @Enumerated
    @NonNull
    private Status status;

    @Column(nullable = false, name = "date_start")
    @NonNull
    private Date dateStart;

    @Column(name = "date_end")
    private Date dateEnd;

    public enum Status {
        ACTIVE, // Employee currently occupies this position
        FINISHED // Employee no longer occupies this position
    }
}
