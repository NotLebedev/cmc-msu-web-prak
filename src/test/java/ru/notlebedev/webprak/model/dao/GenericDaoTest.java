package ru.notlebedev.webprak.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import ru.notlebedev.webprak.model.entity.TestEntity;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenericDaoTest {
    @Autowired
    private TestEntityDao testEntityDao;

    private SessionFactory sessionFactory;

    @Test
    void saveFindTest() {
        TestEntity entity = new TestEntity("Qwe");
        testEntityDao.save(entity);
        Collection<TestEntity> res = testEntityDao.findAll();
        assertEquals(1, res.size());
        assertEquals("Qwe", res.stream().findAny().get().getText());
        entity.setId(1L);
    }

    @AfterEach
    void setup() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE genericDaoTestEntity RESTART IDENTITY;").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }
}