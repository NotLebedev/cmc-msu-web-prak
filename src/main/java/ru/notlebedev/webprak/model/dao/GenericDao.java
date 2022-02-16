package ru.notlebedev.webprak.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.entity.GenericEntity;

import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

@Repository
public abstract class GenericDao<T extends GenericEntity<ID>, ID> {
    private SessionFactory sessionFactory;
    @SuppressWarnings("unchecked")
    Class<T> typeT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    Class<ID> typeID = (Class<ID>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[1];

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    public <S extends T> S save(S entity) {
        // Inserting new entities should not break sequential ordering
        if (entity.getId() != null)
            entity.setId(null);
        try (Session session = sessionFactory.openSession()) {
            session.save(entity);
            return entity;
        }
    }

    Collection<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(typeT);
            criteriaQuery.from(typeT);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
