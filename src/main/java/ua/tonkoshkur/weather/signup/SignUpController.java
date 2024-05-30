package ua.tonkoshkur.weather.signup;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.common.exception.BadRequestException;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;

import java.io.IOException;

@WebServlet("/signup")
public class SignUpController extends BaseServlet {

    private transient SignUpRequestValidator signUpRequestValidator;
    private transient SignUpRequestMapper signUpRequestMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        signUpRequestValidator = new SignUpRequestValidator();
        signUpRequestMapper = new SignUpRequestMapper();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);
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
        WebContext context = buildWebContext(req, resp);
        context.setVariable("signUpRequest", signUpRequest);
        context.setVariable("error", error);
        templateEngine.process("signup", context, resp.getWriter());
    }
}
