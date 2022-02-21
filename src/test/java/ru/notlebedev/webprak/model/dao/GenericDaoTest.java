package ru.notlebedev.webprak.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.test.context.TestPropertySource;
import ru.notlebedev.webprak.model.entity.TestEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations="classpath:test.properties")
class GenericDaoTest {
    @Autowired
    private TestEntityDao testEntityDao;

    private SessionFactory sessionFactory;

    @Test
    void saveFindTest() {
        TestEntity entity0 = new TestEntity("Qwe");
        TestEntity entity1 = new TestEntity("asd");
        TestEntity entity2 = new TestEntity("zxc");
        testEntityDao.save(entity0);
        testEntityDao.save(entity1);
        testEntityDao.save(entity2);
        Collection<TestEntity> res = testEntityDao.findAll();
        assertEquals(3, res.size());
        assertEquals("Qwe", res.stream().findAny().get().getText());
    }

    @Test
    void saveIdUpdateTest() {
        TestEntity entity = new TestEntity("Qwe");
        entity.setId(10L);

        testEntityDao.save(entity);

        assertEquals(1, entity.getId());
    }

    @Test
    void testSaveAll() {
        Collection<TestEntity> entities = new ArrayList<>();
        entities.add(new TestEntity("qwe"));
        entities.add(new TestEntity("qwe"));
        entities.add(new TestEntity("qwe"));

        testEntityDao.saveAll(entities);

        assertEquals(3, testEntityDao.count());
    }

    @Test
    void testById() {
        Collection<TestEntity> entities = new ArrayList<>();
        entities.add(new TestEntity("qwe"));
        entities.add(new TestEntity("asd"));
        entities.add(new TestEntity("zxc"));

        testEntityDao.saveAll(entities);

        Optional<TestEntity> res = testEntityDao.findById(2L);
        assertTrue(res.isPresent());
        assertEquals("asd", res.get().getText());

        assertTrue(testEntityDao.existsById(1L));
        assertTrue(testEntityDao.existsById(2L));
        assertTrue(testEntityDao.existsById(3L));
    }

    @Test
    void testDelete() {
        List<TestEntity> entities = new ArrayList<>();
        entities.add(new TestEntity("qwe"));
        entities.add(new TestEntity("asd"));
        entities.add(new TestEntity("zxc"));

        testEntityDao.saveAll(entities);

        testEntityDao.delete(entities.get(1));
        testEntityDao.delete(entities.get(0));

        assertEquals(1, testEntityDao.count());
        assertEquals("zxc", testEntityDao.findAll().stream().findAny().get().getText());
    }

    @Test
    void testUpdate() {
        List<TestEntity> entities = new ArrayList<>();
        entities.add(new TestEntity("qwe"));
        entities.add(new TestEntity("asd"));
        entities.add(new TestEntity("zxc"));

        testEntityDao.saveAll(entities);

        entities.get(1).setText("Updated");
        testEntityDao.updateSave(entities.get(1));

        assertEquals("Updated", testEntityDao.findById(2L).get().getText());
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