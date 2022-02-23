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
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.sql.Date;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations= "classpath:application.properties")
class PositionHistoryDAOTest {
    @Autowired
    private DepartmentDAO departmentDAO;
    @Autowired
    private PositionDAO positionDAO;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private PositionHistoryDAO positionHistoryDAO;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void testLoad() {
        Collection<PositionHistoryEntry> positionHistory = positionHistoryDAO.findAll();
        assertEquals(2, positionHistory.size());
        for (PositionHistoryEntry positionHistoryEntry : positionHistory) {
            assertNotNull(positionHistoryEntry.getPosition());
            assertNotNull(positionHistoryEntry.getEmployee());
        }
    }

    @BeforeEach
    void setup() {
        Department dep = new Department("Бухгалтерия", Department.Status.ACTIVE);
        Position pos = new Position(dep, "Бухгалтер", "Считает счета на своём счету", Position.Status.ACTIVE);
        Employee emp1 = new Employee("Прасковья Аркадьевна", "ул. Егорьевский проезд, дом 98, квартира 532",
                "Высшее", "Томский Финансовый Университет");
        Employee emp2 = new Employee("Пётр Сергеевич", "ул. Ленина, дом 13, квартира 3",
                "Высшее", "Московский Финансовый Университет");

        departmentDAO.save(dep);
        positionDAO.save(pos);
        employeeDAO.save(emp1);
        employeeDAO.save(emp2);

        PositionHistoryEntry entr1 = new PositionHistoryEntry(pos, emp1, PositionHistoryEntry.Status.FINISHED,
                Date.valueOf("2022-01-01"));
        entr1.setDateEnd(Date.valueOf("2022-02-01"));
        PositionHistoryEntry entr2 = new PositionHistoryEntry(pos, emp2, PositionHistoryEntry.Status.ACTIVE,
                Date.valueOf("2022-02-01"));

        positionHistoryDAO.save(entr1);
        positionHistoryDAO.save(entr2);
    }

    @AfterEach
    void clean() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE departments RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("TRUNCATE employees RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE hibernate_sequence RESTART WITH 1;").executeUpdate();
            session.getTransaction().commit();
        }
    }
}