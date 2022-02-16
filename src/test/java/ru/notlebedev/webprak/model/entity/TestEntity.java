package ru.notlebedev.webprak.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "genericDaoTestEntity")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class TestEntity implements GenericEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @NonNull
    private String text;
}
