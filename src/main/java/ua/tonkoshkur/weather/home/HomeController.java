package ua.tonkoshkur.weather.home;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ua.tonkoshkur.weather.common.util.ThymeleafUtil;

import java.io.IOException;

@WebServlet("/")
public class HomeController extends HttpServlet {

    private transient TemplateEngine templateEngine;
    private transient JakartaServletWebApplication application;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        templateEngine = (TemplateEngine) context.getAttribute(TemplateEngine.class.getSimpleName());
        application = (JakartaServletWebApplication) context.getAttribute(JakartaServletWebApplication.class.getSimpleName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = ThymeleafUtil.buildWebContext(req, resp, application);
        templateEngine.process("home", context, resp.getWriter());
    }
}
