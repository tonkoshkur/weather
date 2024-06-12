package ua.tonkoshkur.weather.location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import ua.tonkoshkur.weather.common.dao.BaseDao;

import java.util.List;

@RequiredArgsConstructor
public class LocationDao extends BaseDao {

    private final EntityManagerFactory entityManagerFactory;

    public List<Location> findAllByUserId(int userId) {
        String sql = "from Location where user.id = :userId";
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery(sql, Location.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    public void save(Location location) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            executeTransactional(entityManager, () -> entityManager.persist(location));
        } catch (ConstraintViolationException ignore) {
            // No need to do anything yet
        }
    }
}
