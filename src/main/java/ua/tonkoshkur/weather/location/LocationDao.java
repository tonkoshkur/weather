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
        String sql = "from Location where user.id = :userId order by name";
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery(sql, Location.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    public void save(Location location) throws LocationAlreadyExistsException {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            executeTransactional(entityManager, () -> entityManager.persist(location));
        } catch (ConstraintViolationException ignore) {
            throw new LocationAlreadyExistsException();
        }
    }

    public void deleteByIdAndUserId(int locationId, int userId) {
        String sql = "delete from Location where id = :locationId and user.id = :userId";
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Query query = entityManager.createQuery(sql)
                    .setParameter("locationId", locationId)
                    .setParameter("userId", userId);
            executeTransactional(entityManager, query::executeUpdate);
        }
    }
}
