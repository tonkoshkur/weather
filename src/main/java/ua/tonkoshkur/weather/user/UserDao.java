package ua.tonkoshkur.weather.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.common.dao.BaseDao;

import java.util.Optional;

@RequiredArgsConstructor
public class UserDao extends BaseDao {

    private final EntityManagerFactory entityManagerFactory;

    public Optional<User> findByLogin(String login) {
        String sql = "from User where login = :login";
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery(sql, User.class)
                    .setParameter("login", login)
                    .getResultStream()
                    .findFirst();
        }
    }

    public User save(User user) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            executeTransactional(entityManager, () -> entityManager.persist(user));
            return user;
        }
    }
}
