package ru.notlebedev.webprak.model.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.ReflectionMagic;
import ru.notlebedev.webprak.model.dao.GenericDAO;
import ru.notlebedev.webprak.model.entity.GenericEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Optional;

@Repository
abstract class GenericDAOImpl<T extends GenericEntity<ID>, ID extends Number>
        implements GenericDAO<T, ID> {
    private SessionFactory sessionFactory;
    Class<T> typeT = ReflectionMagic.getGeneric(getClass(), 0);
    Class<ID> typeID = ReflectionMagic.getGeneric(getClass(), 1);

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    public void save(T entity) {
        // Inserting new entities should not break sequential ordering
        if (entity.getId() != null)
            entity.setId(null);
        try (Session session = sessionFactory.openSession()) {
            session.save(entity);
        }
    }

    public void saveAll(Collection<T> entities) {
        entities.forEach(this::save);
    }

    public Optional<T> findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(typeT, id));
        }
    }

    public boolean existsById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createCriteria(typeT)
                    .add(Restrictions.idEq(id))
                    .setProjection(Projections.id())
                    .uniqueResult() != null;
        }
    }

    public Collection<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(typeT);
            criteriaQuery.from(typeT);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    public long count() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<T> root = criteriaQuery.from(typeT);
            criteriaQuery.select(builder.count(root));
            return session.createQuery(criteriaQuery).getResultList().get(0);
        }
    }

    public void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        }
    }

    public void updateSave(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(entity);
            session.getTransaction().commit();
        }
    }
}
