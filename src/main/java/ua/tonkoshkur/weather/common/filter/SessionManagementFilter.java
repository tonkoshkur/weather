package ua.tonkoshkur.weather.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private SessionDao sessionDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionDao = (SessionDao) filterConfig.getServletContext().getAttribute(SessionDao.class.getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession httpSession = httpRequest.getSession();

        CookieHelper.getSessionId(httpRequest)
                .flatMap(sessionDao::findById)
                .filter(this::isSessionAlive)
                .map(Session::getUser)
                .ifPresentOrElse(user -> httpSession.setAttribute(USER_ATTRIBUTE, user),
                        () -> handleInvalidSession(httpSession));
        chain.doFilter(request, response);
    }

    private boolean isSessionAlive(Session session) {
        return session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    private void handleInvalidSession(HttpSession httpSession) {
        User currentUser = (User) httpSession.getAttribute(USER_ATTRIBUTE);
        if (currentUser != null) {
            httpSession.invalidate();
            throw new ExpiredSessionException();
        }
    }
}
