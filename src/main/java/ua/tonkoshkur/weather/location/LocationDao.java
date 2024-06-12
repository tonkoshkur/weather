package ua.tonkoshkur.weather.location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
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
}
