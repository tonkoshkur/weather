package ua.tonkoshkur.weather.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ua.tonkoshkur.weather.session.Session;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieHelper {

    private static final String SESSION_ID_KEY = "sessionId";

    public static Optional<String> getSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(SESSION_ID_KEY))
                .map(Cookie::getValue)
                .findAny();
    }

    public static void setSessionId(Session session, HttpServletResponse response) {
        int expiry = getExpiry(session);
        Cookie cookie = new Cookie(SESSION_ID_KEY, session.getId());
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
    }

    private static int getExpiry(Session session) {
        return (int) SECONDS.between(LocalDateTime.now(), session.getExpiresAt());
    }
}
