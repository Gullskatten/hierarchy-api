package com.hierarchy.api.controller;

import com.hierarchy.api.entity.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;

public class UserController extends AbstractDAO<User>{

    public UserController(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<User> findAll() {
        return currentSession().createNamedQuery(User.FIND_ALL, User.class).list();
    }

    public User findByHash(String id) {
        List<User> usersFound = currentSession().createNamedQuery(User.FIND_BY_HASH, User.class).setParameter("hash", id).list();
        return usersFound.isEmpty() ? null : usersFound.get(0);
    }

    public List<User> findChildrenByHash(String hash) {
        return currentSession().createNamedQuery(User.FIND_CHILDREN, User.class).setParameter("hash", hash).list();

    }

    public User findById(int id) {
        return currentSession().get(User.class, id);
    }

    public void delete(User person) {
        currentSession().delete(person);
    }

    public void update(User person) {
        currentSession().saveOrUpdate(person);
    }

    public User insert(User person) {
        return persist(person);
    }
}
