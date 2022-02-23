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
    void testSaveLoad() {

    }

    @BeforeEach
    void setup() {

    }

    @AfterEach
    void clean() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE position_history RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE hibernate_sequence RESTART WITH 1;").executeUpdate();
            session.getTransaction().commit();
        }
    }
}