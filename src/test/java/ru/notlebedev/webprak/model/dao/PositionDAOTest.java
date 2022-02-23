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
import ru.notlebedev.webprak.model.entity.Position;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations= "classpath:application.properties")
class PositionDAOTest {
    @Autowired
    private DepartmentDAO departmentDAO;
    @Autowired
    private PositionDAO positionDAO;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void testLoad() {
        Collection<Position> positions = positionDAO.findAll();

        for (Position position : positions) {
            assertNotNull(position.getDepartment());
        }
    }

    @BeforeEach
    void setup() {
        Department dep1 = new Department("Головное", Department.Status.ACTIVE);
        Department dep2 = new Department("Другое", Department.Status.ACTIVE);
        dep2.setDepartmentSuper(dep1);
        Position pos1 = new Position(dep1, "Бухгалтер", "Считает счета на своём счету", Position.Status.ACTIVE);
        Position pos2 = new Position(dep2, "Водитель", "Водит машину", Position.Status.ACTIVE);

        departmentDAO.save(dep1);
        departmentDAO.save(dep2);
        positionDAO.save(pos1);
        positionDAO.save(pos2);
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