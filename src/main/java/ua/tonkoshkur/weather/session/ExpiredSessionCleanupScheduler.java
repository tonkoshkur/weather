package ua.tonkoshkur.weather.session;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ExpiredSessionCleanupScheduler {

    private static final long INITIAL_DELAY = 0;
    private final long purgePeriodMinutes;
    private final SessionDao sessionDao;
    private ScheduledExecutorService executor;

    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::cleanup, INITIAL_DELAY, purgePeriodMinutes, TimeUnit.MINUTES);
    }

    private void cleanup() {
        sessionDao.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    public void stop() {
        executor.shutdown();
    }
}
