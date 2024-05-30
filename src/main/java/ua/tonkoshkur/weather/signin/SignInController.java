package ua.tonkoshkur.weather.signin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;

import java.io.IOException;

@WebServlet("/signin")
public class SignInController extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);
        context.setVariable("signInRequest", new SignInRequest());
        templateEngine.process("signin", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath());
    }
}
