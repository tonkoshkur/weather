package ua.tonkoshkur.weather.home;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.user.User;

import java.io.IOException;

@WebServlet("/")
public class HomeController extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        WebContext context = buildWebContext(req, resp);
        context.setVariable("user", user);
        templateEngine.process("home", context, resp.getWriter());
    }
}
