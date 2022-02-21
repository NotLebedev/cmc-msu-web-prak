package ru.notlebedev.webprak.model.dao;

import ru.notlebedev.webprak.model.entity.GenericEntity;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    /**
     * Initialize all {@link FetchType}.LAZY {@link OneToMany} and {@link ManyToOne} fields
     * if session is in progress initialization will proceed inside that session, otherwise
     * fresh session will be started
     * @param entity entity all fields (described above) of which will be initialized
     */
    void initialize(T entity);
}
