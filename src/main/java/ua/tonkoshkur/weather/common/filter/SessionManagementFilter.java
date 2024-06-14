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
        HttpSession httpSession = httpRequest.getSession();

        CookieHelper.getSessionId(httpRequest)
                .flatMap(sessionDao::findById)
                .filter(this::isSessionAlive)
                .ifPresentOrElse(session -> manageSession(httpSession, session, (HttpServletResponse) response),
                        () -> handleInvalidSession(httpSession));

        chain.doFilter(request, response);
    }

    private boolean isSessionAlive(Session session) {
        return session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    private void manageSession(HttpSession httpSession, Session session, HttpServletResponse response) {
        addUserToSession(httpSession, session.getUser());
        prolongSession(session, response);
    }

    private void addUserToSession(HttpSession httpSession, User user) {
        httpSession.setAttribute(USER_ATTRIBUTE, user);
    }

    private void prolongSession(Session session, HttpServletResponse response) {
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
