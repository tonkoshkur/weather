package ua.tonkoshkur.weather.common.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected transient TemplateEngine templateEngine;
    private transient JakartaServletWebApplication application;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        templateEngine = (TemplateEngine) context.getAttribute(TemplateEngine.class.getSimpleName());
        application = (JakartaServletWebApplication) context.getAttribute(JakartaServletWebApplication.class.getSimpleName());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isHiddenDeleteRequest(req)) {
            doDelete(req, resp);
            return;
        }
        super.service(req, resp);
    }

    private boolean isHiddenDeleteRequest(HttpServletRequest request) {
        String methodParam = request.getParameter("_method");
        return methodParam != null && methodParam.equalsIgnoreCase("delete");
    }

    protected WebContext buildWebContext(HttpServletRequest request, HttpServletResponse response) {
        IServletWebExchange webExchange = application.buildExchange(request, response);
        return new WebContext(webExchange);
    }
}
