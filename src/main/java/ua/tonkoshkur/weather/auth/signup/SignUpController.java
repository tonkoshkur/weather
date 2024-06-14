package ua.tonkoshkur.weather.auth.signup;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.exception.BadRequestException;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.common.util.CookieHelper;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.user.UserAlreadyExistsException;

import java.io.IOException;

@WebServlet("/signup")
public class SignUpController extends BaseServlet {

    private static final String SIGN_UP_PAGE = "signup";
    private static final String SIGN_UP_REQUEST_VARIABLE = "signUpRequest";
    private transient SignUpRequestValidator signUpRequestValidator;
    private transient SignUpRequestMapper signUpRequestMapper;
    private transient AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        authService = (AuthService) context.getAttribute(AuthService.class.getSimpleName());
        signUpRequestValidator = new SignUpRequestValidator();
        signUpRequestMapper = new SignUpRequestMapper();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);
        context.setVariable(SIGN_UP_REQUEST_VARIABLE, new SignUpRequest());
        templateEngine.process(SIGN_UP_PAGE, context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SignUpRequest signUpRequest = signUpRequestMapper.map(req);

        try {
            signUpRequestValidator.validate(signUpRequest);
            Session session = authService.signUp(signUpRequest.login(), signUpRequest.password());
            CookieHelper.setSessionId(session, resp);
            resp.sendRedirect(req.getContextPath());
        } catch (BadRequestException | UserAlreadyExistsException e) {
            handleSignUpError(signUpRequest, e.getMessage(), req, resp);
        }
    }

    private void handleSignUpError(SignUpRequest signUpRequest, String error,
                                   HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WebContext context = buildWebContext(req, resp);
        context.setVariable(SIGN_UP_REQUEST_VARIABLE, signUpRequest);
        context.setVariable("error", error);
        templateEngine.process(SIGN_UP_PAGE, context, resp.getWriter());
    }
}
