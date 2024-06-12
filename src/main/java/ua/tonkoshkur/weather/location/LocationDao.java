package ua.tonkoshkur.weather.location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
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

    public void deleteById(int locationId) {
        String sql = "delete from Location where id = :locationId";
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Query query = entityManager.createQuery(sql)
                    .setParameter("locationId", locationId);
            executeTransactional(entityManager, query::executeUpdate);
        }
    }

}
