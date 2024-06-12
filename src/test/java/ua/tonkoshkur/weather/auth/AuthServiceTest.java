package ua.tonkoshkur.weather.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.tonkoshkur.weather.common.exception.InvalidCredentialsException;
import ua.tonkoshkur.weather.common.factory.ComponentFactory;
import ua.tonkoshkur.weather.common.properties.AppProperties;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;
import ua.tonkoshkur.weather.user.UserAlreadyExistsException;
import ua.tonkoshkur.weather.user.UserDao;

class AuthServiceTest {

    private static final String USER_LOGIN = "login";
    private static final String USER_PASSWORD = "password";
    private static AuthService authService;
    private static SessionDao sessionDao;
    private static UserDao userDao;

    @BeforeAll
    static void setUp() {
        AppProperties appProperties = new AppProperties();
        ComponentFactory componentFactory = new ComponentFactory(appProperties);
        authService = componentFactory.getAuthService();
        sessionDao = componentFactory.getSessionDao();
        userDao = componentFactory.getUserDao();
    }

    @AfterEach
    void clearDatabases() {
        sessionDao.deleteAll();
        userDao.deleteAll();
    }

    @Test
    void givenExistedUser_whenSignIn_thenCreateSession() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);

        Session session = authService.signIn(USER_LOGIN, USER_PASSWORD);

        boolean created = sessionDao.findById(session.getId()).isPresent();
        Assertions.assertTrue(created);
    }

    @Test
    void givenExistedUser_whenSignIn_thenReturnSessionWithThisUser() {
        User user = authService.signUp(USER_LOGIN, USER_PASSWORD).getUser();

        Session session = authService.signIn(USER_LOGIN, USER_PASSWORD);

        Assertions.assertEquals(user, session.getUser());
    }

    @Test
    void givenNewUser_whenSignIn_thenThrowsInvalidCredentialsException() {
        Assertions.assertThrows(InvalidCredentialsException.class, () -> authService.signIn(USER_LOGIN, USER_PASSWORD));
    }

    @Test
    void givenNewUser_whenSignUp_thenCreateUser() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);

        boolean created = userDao.findByLogin(USER_LOGIN).isPresent();
        Assertions.assertTrue(created);
    }

    @Test
    void givenNewUser_whenSignUp_thenCreateSession() {
        Session session = authService.signUp(USER_LOGIN, USER_PASSWORD);

        boolean created = sessionDao.findById(session.getId()).isPresent();
        Assertions.assertTrue(created);
    }

    @Test
    void givenExistingUser_whenSignUp_thenThrowsUserAlreadyExistsException() {
        authService.signUp(USER_LOGIN, USER_PASSWORD);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> authService.signUp(USER_LOGIN, USER_PASSWORD));
    }

    @Test
    void givenExistingSession_whenSignOut_thenDeleteSession() {
        Session session = authService.signUp(USER_LOGIN, USER_PASSWORD);

        authService.signOut(session.getId());

        boolean deleted = sessionDao.findById(session.getId()).isEmpty();
        Assertions.assertTrue(deleted);
    }
}
