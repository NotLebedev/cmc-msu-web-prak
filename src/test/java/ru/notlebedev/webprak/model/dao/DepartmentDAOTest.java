package ru.notlebedev.webprak.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.notlebedev.webprak.model.entity.Department;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.notlebedev.webprak.model.entity.Department.Status.ACTIVE;
import static ru.notlebedev.webprak.model.entity.Department.Status.DEFUNCT;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations="classpath:test.properties")
public class DepartmentDAOTest {
    @Autowired
    private DepartmentDAO departmentDAO;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void testGetByName() {
        Collection<Department> res = departmentDAO.getDepartmentByName("Заготов");

        List<String> namesExpected = new ArrayList<>();
        namesExpected.add("Заготовка копыт");
        namesExpected.add("Заготовка рогов");
        namesExpected.add("Заготовки");
        namesExpected.add("Заготовка хвостов");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toList()));
    }

    @Test
    void testGetByNameByStatus0() {
        Collection<Department> res = departmentDAO.getDepartmentByNameByStatus("Заготов", ACTIVE);

        List<String> namesExpected = new ArrayList<>();
        namesExpected.add("Заготовка копыт");
        namesExpected.add("Заготовка рогов");
        namesExpected.add("Заготовки");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toList()));
    }

    @Test
    void testGetByNameByStatus1() {
        Collection<Department> res = departmentDAO.getDepartmentByNameByStatus("Заготов", DEFUNCT);

        List<String> namesExpected = new ArrayList<>();
        namesExpected.add("Заготовка хвостов");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toList()));
    }

    @Test
    void testHierarchy() {
        Department dep1 = departmentDAO.findById(1L).get();
        departmentDAO.initialize(dep1);
        Collection<Department> res1 = dep1.getChildren();
        assertEquals(Set.of("Бухгалтерия", "Заготовки"),
                res1.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @BeforeEach
    void setup() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department("ООО \"Рога и Копыта\"", ACTIVE));
        departments.add(new Department("Бухгалтерия", ACTIVE));
        departments.add(new Department("Заготовка копыт", ACTIVE));
        departments.add(new Department("Заготовка рогов", ACTIVE));
        departments.add(new Department("Заготовки", ACTIVE));

        departments.add(new Department("Пиар", DEFUNCT));
        departments.add(new Department("Заготовка хвостов", DEFUNCT));

        departments.get(1).setDepartmentSuper(departments.get(0));
        departments.get(4).setDepartmentSuper(departments.get(0));
        departments.get(2).setDepartmentSuper(departments.get(4));
        departments.get(3).setDepartmentSuper(departments.get(4));
        departmentDAO.saveAll(departments);
    }

    @AfterEach
    void clean() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE departments RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE hibernate_sequence RESTART WITH 1;").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
