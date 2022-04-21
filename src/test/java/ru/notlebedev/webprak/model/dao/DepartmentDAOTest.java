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

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.notlebedev.webprak.model.entity.Department.Status.ACTIVE;
import static ru.notlebedev.webprak.model.entity.Department.Status.DEFUNCT;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations= "classpath:application.properties")
public class DepartmentDAOTest {
    @Autowired
    private DepartmentDAO departmentDAO;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void testEmptyFilter() {
        DepartmentDAO.Filter filter = DepartmentDAO.getFilterBuilder().build();
        Collection<Department> res = departmentDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("ООО \"Рога и Копыта\"");
        namesExpected.add("Бухгалтерия");
        namesExpected.add("Заготовка копыт");
        namesExpected.add("Заготовка рогов");
        namesExpected.add("Заготовки");
        namesExpected.add("Заготовка хвостов");
        namesExpected.add("Пиар");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @Test
    void testGetByName() {
        DepartmentDAO.Filter filter = DepartmentDAO.getFilterBuilder()
                .name("Заготов").build();
        Collection<Department> res = departmentDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Заготовка копыт");
        namesExpected.add("Заготовка рогов");
        namesExpected.add("Заготовки");
        namesExpected.add("Заготовка хвостов");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @Test
    void testGetByNameByStatus0() {
        DepartmentDAO.Filter filter = DepartmentDAO.getFilterBuilder()
                .name("Заготов").status(ACTIVE).build();
        Collection<Department> res = departmentDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Заготовка копыт");
        namesExpected.add("Заготовка рогов");
        namesExpected.add("Заготовки");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @Test
    void testGetByNameByStatus1() {
        DepartmentDAO.Filter filter = DepartmentDAO.getFilterBuilder()
                        .name("Заготов").status(DEFUNCT).build();
        Collection<Department> res = departmentDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Заготовка хвостов");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @Test
    void testGetByStatus() {
        DepartmentDAO.Filter filter = DepartmentDAO.getFilterBuilder()
                .status(ACTIVE).build();
        Collection<Department> res = departmentDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("ООО \"Рога и Копыта\"");
        namesExpected.add("Бухгалтерия");
        namesExpected.add("Заготовка копыт");
        namesExpected.add("Заготовка рогов");
        namesExpected.add("Заготовки");

        assertEquals(namesExpected, res.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @Test
    void testInitialization() {
        assertTrue(departmentDAO.findById(1L).isPresent());
        Department dep1 = departmentDAO.findById(1L).get();

        for (Department child : dep1.getChildren()) {
            assertDoesNotThrow(() -> departmentDAO.initialize(child));
        }

        Collection<Department> res1 = dep1.getChildren();
        assertEquals(Set.of("Бухгалтерия", "Заготовки"),
                res1.stream().map(Department::getName).collect(Collectors.toSet()));
    }

    @Test
    void testGetHierarchy() {
        Collection<Department> heads = departmentDAO.getHierarchy();
        assertEquals(3, heads.size());

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

        departments.get(0).setChildren(Set.of(departments.get(1), departments.get(4)));
        departments.get(1).setChildren(new HashSet<>());
        departments.get(2).setChildren(new HashSet<>());
        departments.get(3).setChildren(new HashSet<>());
        departments.get(4).setChildren(Set.of(departments.get(2), departments.get(3)));

        Department head = heads.stream().findAny().orElse(null);
        assertNotNull(head);

        assertThat(head)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*id", ".*positions")
                .isEqualTo(departments.get(0));
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
