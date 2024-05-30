package ua.tonkoshkur.weather.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ua.tonkoshkur.weather.common.exception.DatabaseException;

public abstract class BaseDao {
    protected void executeTransactional(EntityManager entityManager, Runnable runnable) throws DatabaseException {
        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        try {
            runnable.run();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new DatabaseException(e);
        }
    }
}
