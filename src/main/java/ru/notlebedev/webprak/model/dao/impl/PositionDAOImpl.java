package ru.notlebedev.webprak.model.dao.impl;

import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.entity.Position;

@Repository
public class PositionDAOImpl extends GenericDAOImpl<Position, Long>
    implements PositionDAO {
}
