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
import ru.notlebedev.webprak.model.entity.Employee;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations= "classpath:application.properties")
public class EmployeeDAOTest {
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void testEmptyFilter() {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder().build();
        Collection<Employee> res = employeeDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Иван Иванович");
        namesExpected.add("Прасковья Аркадьевна");
        namesExpected.add("Христофор Геннадиевич");
        namesExpected.add("Иван Петрович");

        assertEquals(namesExpected, res.stream().map(Employee::getName).collect(Collectors.toSet()));
    }

    @Test
    void testNameFilter() {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .name("Иван").build();
        Collection<Employee> res = employeeDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Иван Иванович");
        namesExpected.add("Иван Петрович");

        assertEquals(namesExpected, res.stream().map(Employee::getName).collect(Collectors.toSet()));
    }

    @Test
    void testAddressFilter() {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .address("Кедровая").build();
        Collection<Employee> res = employeeDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Иван Иванович");
        namesExpected.add("Иван Петрович");

        assertEquals(namesExpected, res.stream().map(Employee::getName).collect(Collectors.toSet()));
    }

    @Test
    void testEducationLevelFilter() {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .educationLevel("Среднее").build();
        Collection<Employee> res = employeeDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Иван Иванович");
        namesExpected.add("Христофор Геннадиевич");
        namesExpected.add("Иван Петрович");

        assertEquals(namesExpected, res.stream().map(Employee::getName).collect(Collectors.toSet()));
    }

    @Test
    void testEducationPlaceFilter() {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .educationPlace("Техникум").build();
        Collection<Employee> res = employeeDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Иван Иванович");
        namesExpected.add("Христофор Геннадиевич");
        namesExpected.add("Иван Петрович");

        assertEquals(namesExpected, res.stream().map(Employee::getName).collect(Collectors.toSet()));
    }

    @Test
    void testAllFilter() {
        EmployeeDAO.Filter filter = EmployeeDAO.getFilterBuilder()
                .name("Иван")
                .address("дом 48")
                .educationLevel("Среднее")
                .educationPlace("Техникум").build();
        Collection<Employee> res = employeeDAO.getByFilter(filter);

        Set<String> namesExpected = new HashSet<>();
        namesExpected.add("Иван Иванович");

        assertEquals(namesExpected, res.stream().map(Employee::getName).collect(Collectors.toSet()));
    }

    @Test
    void testGetKnownEducationLevels() {
        Collection<String> res = employeeDAO.getKnownEducationLevels();

        Set<String> levelsExpected = new HashSet<>();
        levelsExpected.add("Среднее");
        levelsExpected.add("Высшее");

        assertThat(levelsExpected)
                .isEqualTo(res);
    }

    @Test
    void testGetKnownEducationPlaces() {
        Collection<String> res = employeeDAO.getKnownEducationPlaces();

        Set<String> placesExpected = new HashSet<>();
        placesExpected.add("Московский Аграрный Техникум");
        placesExpected.add("Томский Финансовый Университет");
        placesExpected.add("Новгородский Макароноварительный Техникум");

        assertThat(placesExpected)
                .isEqualTo(res);
    }

    @BeforeEach
    void setup() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Иван Иванов", "ул. Кедровая, дом 48, квартира 97",
                "Среднее", "Московский Аграрный Техникум"));
        employees.add(new Employee("Прасковья Аркадьевна", "ул. Егорьевский проезд, дом 98, квартира 532",
                "Высшее", "Томский Финансовый Университет"));
        employees.add(new Employee("Христофор Геннадиевич", "ул. Кунцевская, дом 7, квартира 533",
                "Среднее", "Новгородский Макароноварительный Техникум"));
        employees.add(new Employee("Иван Петров", "ул. Кедровая, дом 45, квартира 6",
                "Среднее", "Московский Аграрный Техникум"));
    }

    @AfterEach
    void clean() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE employees RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE hibernate_sequence RESTART WITH 1;").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
