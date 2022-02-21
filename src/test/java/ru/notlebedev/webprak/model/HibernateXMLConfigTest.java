package ru.notlebedev.webprak.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
class HibernateXMLConfigTest {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Test
    public void test() {
        SessionFactory sessionFactoryObject = sessionFactory.getObject();
        Session session = sessionFactoryObject.openSession();
    }
}