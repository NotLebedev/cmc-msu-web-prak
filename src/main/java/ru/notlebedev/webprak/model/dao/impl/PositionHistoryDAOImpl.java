package ru.notlebedev.webprak.model.dao.impl;

import org.springframework.stereotype.Repository;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

@Repository
public class PositionHistoryDAOImpl extends GenericDAOImpl<PositionHistoryEntry, Long>
    implements PositionHistoryDAO {
}
