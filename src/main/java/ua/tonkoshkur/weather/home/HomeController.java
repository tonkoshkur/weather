package ua.tonkoshkur.weather.home;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;

import java.io.IOException;

@WebServlet("/")
public class HomeController extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);
        templateEngine.process("home", context, resp.getWriter());
    }
}
