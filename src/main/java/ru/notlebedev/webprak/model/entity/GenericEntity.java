package ru.notlebedev.webprak.model.entity;

public interface GenericEntity<ID> {
    ID getId();
    void setId(ID id);
}
