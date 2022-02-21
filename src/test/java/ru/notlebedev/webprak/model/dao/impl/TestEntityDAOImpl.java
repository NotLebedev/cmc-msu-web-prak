package ru.notlebedev.webprak.model.dao.impl;


import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.dao.TestEntityDAO;
import ru.notlebedev.webprak.model.entity.TestEntity;

@Repository
class TestEntityDAOImpl extends GenericDAOImpl<TestEntity, Long>
        implements TestEntityDAO {
}
