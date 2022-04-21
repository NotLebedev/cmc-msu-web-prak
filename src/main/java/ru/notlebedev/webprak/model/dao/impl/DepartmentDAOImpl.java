package ru.notlebedev.webprak.model.dao.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.entity.Department;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class DepartmentDAOImpl extends GenericDAOImpl<Department, Long>
    implements DepartmentDAO {
    @Override
    public Collection<Department> getByFilter(Filter filter) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Department> criteriaQuery = builder.createQuery(Department.class);
            Root<Department> root = criteriaQuery.from(Department.class);

            List<Predicate> predicates = new ArrayList<>();
            if (filter.getName() != null) {
                String pattern = "%" + filter.getName() + "%"; // Any name containing substring
                predicates.add(builder.like(root.get("name"), pattern));
            }
            if (filter.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), filter.getStatus()));
            }

            if (predicates.size() != 0)
                criteriaQuery.where(predicates.toArray(new Predicate[0]));

            return applyInitialize(session.createQuery(criteriaQuery).getResultList());
        }
    }

    @Override
    public Collection<Department> getHierarchy() {
        Collection<Department> departments = findAll();

        return departments.stream()
                .filter(department -> Objects.isNull(department.getDepartmentSuper()))
                .collect(Collectors.toList());
    }
}
