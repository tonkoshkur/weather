package ua.tonkoshkur.weather.signup;

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
import ua.tonkoshkur.weather.common.exception.BadRequestException;
import ua.tonkoshkur.weather.common.util.ThymeleafUtil;

import java.io.IOException;

@WebServlet("/signup")
public class SignUpController extends HttpServlet {

    private transient TemplateEngine templateEngine;
    private transient JakartaServletWebApplication application;
    private transient SignUpRequestValidator signUpRequestValidator;
    private transient SignUpRequestMapper signUpRequestMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        templateEngine = (TemplateEngine) context.getAttribute(TemplateEngine.class.getSimpleName());
        application = (JakartaServletWebApplication) context.getAttribute(JakartaServletWebApplication.class.getSimpleName());
        signUpRequestValidator = new SignUpRequestValidator();
        signUpRequestMapper = new SignUpRequestMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = ThymeleafUtil.buildWebContext(req, resp, application);
        context.setVariable("signUpRequest", new SignUpRequest());
        templateEngine.process("signup", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SignUpRequest signUpRequest = signUpRequestMapper.map(req);

        try {
            signUpRequestValidator.validate(signUpRequest);
        } catch (BadRequestException e) {
            handleSignUpError(signUpRequest, e.getMessage(), req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath());
    }

    private void handleSignUpError(SignUpRequest signUpRequest, String error,
                                   HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WebContext context = ThymeleafUtil.buildWebContext(req, resp, application);
        context.setVariable("signUpRequest", signUpRequest);
        context.setVariable("error", error);
        templateEngine.process("signup", context, resp.getWriter());
    }
}
