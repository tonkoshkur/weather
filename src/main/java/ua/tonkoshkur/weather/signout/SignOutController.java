package ua.tonkoshkur.weather.signout;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.common.util.CookieHelper;

import java.io.IOException;

@WebServlet("/signout")
public class SignOutController extends BaseServlet {

    private transient AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        authService = (AuthService) context.getAttribute(AuthService.class.getSimpleName());
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookieHelper.getSessionId(req)
                .ifPresent(sessionId -> authService.signOut(sessionId));
        resp.sendRedirect(req.getContextPath());
    }
}
