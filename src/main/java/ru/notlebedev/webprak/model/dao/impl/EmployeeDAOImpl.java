package ru.notlebedev.webprak.model.dao.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.entity.Employee;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class EmployeeDAOImpl extends GenericDAOImpl<Employee, Long>
    implements EmployeeDAO {
    @Override
    public Collection<Employee> getByFilter(Filter filter) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);
            Root<Employee> root = criteriaQuery.from(Employee.class);

            List<Predicate> predicates = new ArrayList<>();
            if (filter.getName() != null)
                predicates.add(builder.like(root.get("name"), getPattern(filter.getName())));
            if (filter.getAddress() != null)
                predicates.add(builder.like(root.get("address"), getPattern(filter.getAddress())));
            if (filter.getEducationLevel() != null)
                predicates.add(builder.like(root.get("educationLevel"), getPattern(filter.getEducationLevel())));
            if (filter.getEducationPlace() != null)
                predicates.add(builder.like(root.get("educationPlace"), getPattern(filter.getEducationPlace())));

            if (predicates.size() != 0)
                criteriaQuery.where(predicates.toArray(new Predicate[0]));

            return applyInitialize(session.createQuery(criteriaQuery).getResultList());
        }
    }

    private String getPattern(String search) {
        return "%" + search + "%";
    }

    @Override
    public Collection<String> getKnownEducationLevels() {
        return null;
    }

    @Override
    public Collection<String> getKnownEducationPlaces() {
        return null;
    }
}
