package ua.tonkoshkur.weather.session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.common.BaseDao;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class SessionDao extends BaseDao {

    private final EntityManagerFactory entityManagerFactory;

    public Optional<Session> findById(String sessionId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Session session = entityManager.find(Session.class, sessionId);
            return Optional.ofNullable(session);
        }
    }

    public Session save(Session session) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            executeTransactional(entityManager, () -> entityManager.persist(session));
            return session;
        }
    }

    public void deleteById(String sessionId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            executeTransactional(entityManager, () -> {
                Session session = entityManager.find(Session.class, sessionId);
                entityManager.remove(session);
            });
        }
    }

    public void deleteByExpiresAtBefore(LocalDateTime dateTime) {
        String sql = "delete from Session s where expiresAt < :dateTime";
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Query query = entityManager.createQuery(sql)
                    .setParameter("dateTime", dateTime);
            executeTransactional(entityManager, query::executeUpdate);
        }
    }
}
