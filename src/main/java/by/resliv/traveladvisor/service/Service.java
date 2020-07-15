package by.resliv.traveladvisor.service;

import java.util.List;

public interface Service<T> {
    T save(T t);

    T update(T t);

    void delete(long id);

    T getById(long id);

    List<T> findAll();
}
