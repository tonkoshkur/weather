package ua.tonkoshkur.weather.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.factory.ComponentFactory;
import ua.tonkoshkur.weather.common.util.AppProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class ExpiredSessionCleanupSchedulerTest {

    private static long sessionExpirationMinutes;
    private static long expiredSessionsCleanupMinutes;
    private static SessionDao sessionDao;
    private static AuthService authService;
    private static ExpiredSessionCleanupScheduler expiredSessionCleanupScheduler;
    private Session activeSession;

    @BeforeAll
    static void setUp() {
        AppProperties appProperties = new AppProperties();
        sessionExpirationMinutes = appProperties.getSessionExpirationMinutes();
        expiredSessionsCleanupMinutes = appProperties.getExpiredSessionsCleanupMinutes();
        ComponentFactory componentFactory = new ComponentFactory(appProperties);
        sessionDao = componentFactory.getSessionDao();
        authService = componentFactory.getAuthService();
        expiredSessionCleanupScheduler = componentFactory.getExpiredSessionCleanupScheduler();
    }

    @BeforeEach
    void startScheduler() {
        activeSession = authService.signUp("login", "password");
        expiredSessionCleanupScheduler.start();
    }

    @AfterEach
    void stopScheduler() {
        expiredSessionCleanupScheduler.stop();
    }

    @Test
    void givenActiveSession_whenExistsMoreThanExpirationTime_thenDeleteSession() {
        Duration timeout = Duration.ofMinutes(expiredSessionsCleanupMinutes)
                .plusSeconds(1);
        await().atMost(timeout)
                .pollDelay(sessionExpirationMinutes, TimeUnit.MINUTES)
                .until(() -> sessionDao.findById(activeSession.getId()).isEmpty());
    }
}
