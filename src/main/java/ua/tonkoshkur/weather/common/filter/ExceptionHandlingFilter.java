package ua.tonkoshkur.weather.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(ExceptionHandlingFilter.class.getName());

    protected TemplateEngine templateEngine;
    private JakartaServletWebApplication application;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        templateEngine = (TemplateEngine) context.getAttribute(TemplateEngine.class.getSimpleName());
        application = (JakartaServletWebApplication) context.getAttribute(JakartaServletWebApplication.class.getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            handleException((HttpServletRequest) request, (HttpServletResponse) response, e);
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
        redirectToErrorPage(request, response, exception);
    }

    private void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        WebContext context = buildWebContext(request, response);
        context.setVariable("error", exception.getMessage());
        templateEngine.process("error", context, response.getWriter());
    }

    protected WebContext buildWebContext(HttpServletRequest request, HttpServletResponse response) {
        IServletWebExchange webExchange = application.buildExchange(request, response);
        return new WebContext(webExchange);
    }
}
