package ua.tonkoshkur.weather.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ua.tonkoshkur.weather.common.properties.AppProperties;
import ua.tonkoshkur.weather.common.util.CookieHelper;
import ua.tonkoshkur.weather.session.ExpiredSessionException;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

@WebFilter("/*")
public class SessionManagementFilter implements Filter {

    private static final String USER_ATTRIBUTE = "user";
    private long sessionTtlMinutes;
    private SessionDao sessionDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        AppProperties appProperties = (AppProperties) filterConfig.getServletContext()
                .getAttribute(AppProperties.class.getSimpleName());
        sessionTtlMinutes = appProperties.getSessionTtlMinutes();
        sessionDao = (SessionDao) filterConfig.getServletContext().getAttribute(SessionDao.class.getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (!isAuthRequest(httpRequest)) {
            manageSession(httpRequest, (HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }

    private boolean isAuthRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth/");
    }

    private void manageSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession();
        CookieHelper.getSessionId(request)
                .flatMap(sessionDao::findById)
                .filter(this::isSessionAlive)
                .ifPresentOrElse(session -> handleValidSession(httpSession, session, response),
                        () -> handleInvalidSession(httpSession));
    }

    private boolean isSessionAlive(Session session) {
        return session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    private void handleValidSession(HttpSession httpSession, Session session, HttpServletResponse response) {
        setSessionUser(httpSession, session.getUser());
        extendSession(session, response);
    }

    private void setSessionUser(HttpSession httpSession, User user) {
        httpSession.setAttribute(USER_ATTRIBUTE, user);
    }

    private void extendSession(Session session, HttpServletResponse response) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(sessionTtlMinutes);
        session.setExpiresAt(expiresAt);
        Session updatedSession = sessionDao.update(session);

        CookieHelper.setSessionId(updatedSession, response);
    }

    private void handleInvalidSession(HttpSession httpSession) {
        User currentUser = (User) httpSession.getAttribute(USER_ATTRIBUTE);
        if (currentUser != null) {
            httpSession.invalidate();
            throw new ExpiredSessionException();
        }
    }
}
