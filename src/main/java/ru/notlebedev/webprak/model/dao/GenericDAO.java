package ru.notlebedev.webprak.model.dao;

import ru.notlebedev.webprak.model.entity.GenericEntity;

import java.util.Collection;
import java.util.Optional;

public interface GenericDAO<T extends GenericEntity<ID>, ID extends Number> {
    void save(T entity);

    void saveAll(Collection<T> entities);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Collection<T> findAll();

    long count();

    void delete(T entity);

    void updateSave(T entity);

    void initialize(T entity);
}
