package ua.tonkoshkur.weather.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ua.tonkoshkur.weather.common.util.CookieHelper;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;

import java.io.IOException;
import java.time.LocalDateTime;

@WebFilter("/*")
public class AddSessionUserFilter implements Filter {

    private SessionDao sessionDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionDao = (SessionDao) filterConfig.getServletContext().getAttribute(SessionDao.class.getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession httpSession = httpRequest.getSession();

        CookieHelper.getSessionId(httpRequest)
                .flatMap(sessionDao::findById)
                .filter(this::isSessionAlive)
                .map(Session::getUser)
                .ifPresentOrElse(user -> httpSession.setAttribute("user", user),
                        httpSession::invalidate);

        chain.doFilter(httpRequest, httpResponse);
    }

    private boolean isSessionAlive(Session session) {
        return session.getExpiresAt().isAfter(LocalDateTime.now());
    }
}
