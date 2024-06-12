package ua.tonkoshkur.weather.common.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.exception.ConstraintViolationException;
import ua.tonkoshkur.weather.common.exception.DatabaseException;

public abstract class BaseDao {
    protected void executeTransactional(EntityManager entityManager, Runnable runnable)
            throws ConstraintViolationException, DatabaseException {

        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        try {
            runnable.run();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            if (e instanceof ConstraintViolationException constraintViolationException) {
                throw constraintViolationException;
            }
            throw new DatabaseException(e);
        }
    }
}
