package ua.tonkoshkur.weather.session;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class OldSessionCleanupScheduler {

    private static final long PURGE_PERIOD_HOURS = 1;
    private static final long INITIAL_DELAY = 0;
    private final SessionDao sessionDao;
    private ScheduledExecutorService executor;

    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::cleanup, INITIAL_DELAY, PURGE_PERIOD_HOURS, TimeUnit.HOURS);
    }

    private void cleanup() {
        sessionDao.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    public void stop() {
        executor.shutdown();
    }
}
