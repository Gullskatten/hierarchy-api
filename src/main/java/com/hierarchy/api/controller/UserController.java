package com.hierarchy.api.controller;

import com.hierarchy.api.entity.SecretValidationAttempt;
import com.hierarchy.api.entity.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.sql.Timestamp;
import java.util.List;

public class UserController extends AbstractDAO<User> {

    private static final int LOCKED_AMOUNT = 9000;
    public static final int MAX_ATTEMPTS_SECRET = 3;

    public UserController(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<User> findAll() {
        return currentSession().createNamedQuery(User.FIND_ALL, User.class).list();
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

    public User findByToken(String token) {
        List<User> usersFound = currentSession().createNamedQuery(User.FIND_BY_HASH, User.class).setParameter("token", token).list();
        return usersFound.isEmpty() ? null : usersFound.get(0);
    }

    public List<User> findChildrenByHash(String token) {
        return currentSession().createNamedQuery(User.FIND_CHILDREN_OF_HASH, User.class).setParameter("token", token).list();
    }

    public boolean validateHashAndSecret(String token, String secret) {
        List<String> string = currentSession()
                .createNamedQuery(User.VALIDATE_TOKEN_AND_SECRET, String.class).setParameter("token", token)
                .setParameter("secret", secret).list();

        return !string.isEmpty();
    }

    public boolean incrementTokenAttempt(User user) {

        List<SecretValidationAttempt> validationAttempts = currentSession().createNamedQuery(SecretValidationAttempt.FIND_BY_USER_ID, SecretValidationAttempt.class)
                .setParameter("userId", user.getUserId()).list();

        if(validationAttempts.isEmpty()) {
            SecretValidationAttempt validationAttempt = new SecretValidationAttempt();
            validationAttempt.setAmount(1);
            validationAttempt.setUserId(user.getUserId());
            validationAttempt.setLastAttempt(new Timestamp(System.currentTimeMillis()));
            currentSession().save(validationAttempt);
        } else {
            SecretValidationAttempt attempt =  validationAttempts.get(0);

            if(System.currentTimeMillis() > attempt.getLastAttempt().getTime() + LOCKED_AMOUNT) {
                attempt.setLastAttempt(new Timestamp(System.currentTimeMillis()));
                attempt.setAmount(1);
                currentSession().merge(attempt);
            } else if(attempt.getAmount() >= MAX_ATTEMPTS_SECRET -1) {
                attempt.setLastAttempt(new Timestamp(System.currentTimeMillis()));
                currentSession().merge(attempt);
                return false;
            } else {
                attempt.setAmount(attempt.getAmount() +1);
                attempt.setLastAttempt(new Timestamp(System.currentTimeMillis()));
                currentSession().merge(attempt);
            }
        }

        return true;
    }
    public void resetTokenAttempts(User user) {
        List<SecretValidationAttempt> validationAttempts = currentSession().createNamedQuery(SecretValidationAttempt.FIND_BY_USER_ID, SecretValidationAttempt.class)
                .setParameter("userId", user.getUserId()).list();

        if(!validationAttempts.isEmpty()) {
            SecretValidationAttempt attempt =  validationAttempts.get(0);

            attempt.setAmount(0);
            attempt.setLastAttempt(new Timestamp(System.currentTimeMillis()));
            currentSession().merge(attempt);
        }
    }

    public void lockUserToken(User user) {
        user.setLockedUntil(new Timestamp(System.currentTimeMillis() + LOCKED_AMOUNT));
        user.setLocked(true);
        currentSession().merge(user);
    }


    public void unlockUserToken(User user) {
        user.setLockedUntil(null);
        user.setLocked(false);
        currentSession().merge(user);
    }

    public int findSecretValidationAttemptsLeft(User user) {
       Integer i = currentSession()
               .createNamedQuery(SecretValidationAttempt.FIND_AMOUNT_ATTEMPTS_LEFT, Integer.class)
               .setParameter("userId", user.getUserId())
               .list().get(0);

       return MAX_ATTEMPTS_SECRET - i;
    }
}
