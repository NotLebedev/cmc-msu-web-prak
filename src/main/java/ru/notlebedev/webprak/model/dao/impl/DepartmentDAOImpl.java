package ru.notlebedev.webprak.model.dao.impl;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.entity.Department;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;

@Repository
public class DepartmentDAOImpl extends GenericDAOImpl<Department, Long>
    implements DepartmentDAO {
    @Override
    public Collection<Department> getDepartmentByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Department> criteriaQuery = builder.createQuery(Department.class);
            Root<Department> root = criteriaQuery.from(Department.class);

            String pattern = "%" + name + "%"; // Any name containing substring
            criteriaQuery.where(builder.like(root.get("name"), pattern));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public Collection<Department> getDepartmentByNameByStatus(String name, Department.Status status) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Department> criteriaQuery = builder.createQuery(Department.class);
            Root<Department> root = criteriaQuery.from(Department.class);

            String pattern = "%" + name + "%"; // Any name containing substring
            criteriaQuery.where(builder.and(
                    builder.like(root.get("name"), pattern),
                    builder.equal(root.get("status"), status)));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public void initialize(Department entity) {
        try (Session session = sessionFactory.openSession()) {
            Department reattachedEntity = (Department) session.merge(entity);
            Hibernate.initialize(reattachedEntity.getChildren());
            Hibernate.initialize(reattachedEntity.getDepartmentSuper());
            Hibernate.initialize(reattachedEntity.getPositions());

            entity.setDepartmentSuper(reattachedEntity.getDepartmentSuper());
            entity.setChildren(reattachedEntity.getChildren());
            entity.setPositions(reattachedEntity.getPositions());
        }
    }
}
