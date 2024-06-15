package ua.tonkoshkur.weather.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tonkoshkur.weather.common.properties.AppProperties;
import ua.tonkoshkur.weather.session.ExpiredSessionException;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionManagementFilterTest {

    private final SessionManagementFilter filter = new SessionManagementFilter();
    private final AppProperties appProperties = new AppProperties();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpSession httpSession;
    @Mock
    private FilterConfig filterConfig;
    @Mock
    private ServletContext servletContext;
    @Mock
    private SessionDao sessionDao;

    @Test
    void doFilter_withAuthRequests_doesNotManageSession() {
        assertDoesNotManageSessionForServletPath("/auth/signin");
        assertDoesNotManageSessionForServletPath("/auth/signup");
        assertDoesNotManageSessionForServletPath("/auth/signout");
    }

    private void assertDoesNotManageSessionForServletPath(String servletPath) {
        when(request.getServletPath()).thenReturn(servletPath);
        assertThatNoException().isThrownBy(() -> filter.doFilter(request, response, filterChain));
        verify(request, never()).getSession();
    }

    @Test
    void doFilter_withNoActiveSession_doesNotThrowExceptions() {
        setUpFilterInitialization();
        when(httpSession.getAttribute(anyString())).thenReturn(null);

        assertThatNoException().isThrownBy(() -> filter.doFilter(request, response, filterChain));
    }

    @Test
    void doFilter_withExpiredSession_throwsExpiredSessionException() {
        setUpFilterInitialization();
        when(httpSession.getAttribute(anyString())).thenReturn(new User());

        assertThrows(ExpiredSessionException.class, () -> filter.doFilter(request, response, filterChain));
    }

    @Test
    void doFilter_withSessionIdInCookies_extendsSession() {
        setUpFilterInitialization();
        setUpValidSessionHandling();

        assertThatNoException().isThrownBy(() -> filter.doFilter(request, response, filterChain));

        verify(sessionDao, atLeastOnce()).update(any(Session.class));
    }

    @Test
    void doFilter_withSessionIdInCookies_setsSessionUser() {
        setUpFilterInitialization();
        setUpValidSessionHandling();

        assertThatNoException().isThrownBy(() -> filter.doFilter(request, response, filterChain));

        verify(httpSession, only()).setAttribute(anyString(), any(User.class));
    }

    private void setUpFilterInitialization() {
        when(filterConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(SessionDao.class.getSimpleName())).thenReturn(sessionDao);
        when(servletContext.getAttribute(AppProperties.class.getSimpleName())).thenReturn(appProperties);
        when(request.getServletPath()).thenReturn("/");
        when(request.getSession()).thenReturn(httpSession);

        assertThatNoException().isThrownBy(() -> filter.init(filterConfig));
    }

    private void setUpValidSessionHandling() {
        LocalDateTime sessionExpiry = LocalDateTime.now().plusMinutes(1);
        Session session = new Session(new User(), sessionExpiry);
        when(sessionDao.findById(anyString())).thenReturn(Optional.of(session));
        when(sessionDao.update(any())).thenReturn(session);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", "1")});
    }
}
