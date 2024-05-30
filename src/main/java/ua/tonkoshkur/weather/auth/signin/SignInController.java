package ua.tonkoshkur.weather.auth.signin;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.auth.AuthService;
import ua.tonkoshkur.weather.common.exception.UnauthorizedException;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.common.util.CookieHelper;
import ua.tonkoshkur.weather.session.Session;

import java.io.IOException;

@WebServlet("/signin")
public class SignInController extends BaseServlet {

    private static final String SIGN_IN_PAGE = "signin";
    private static final String SIGN_IN_REQUEST_VARIABLE = "signInRequest";
    private transient SignInRequestMapper signInRequestMapper;
    private transient AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        authService = (AuthService) context.getAttribute(AuthService.class.getSimpleName());
        signInRequestMapper = new SignInRequestMapper();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);
        context.setVariable(SIGN_IN_REQUEST_VARIABLE, new SignInRequest());
        templateEngine.process(SIGN_IN_PAGE, context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SignInRequest signInRequest = signInRequestMapper.map(req);

        try {
            Session session = authService.signIn(signInRequest.login(), signInRequest.password());
            CookieHelper.setSessionId(session, resp);
            resp.sendRedirect(req.getContextPath());
        } catch (UnauthorizedException e) {
            handleUnauthorized(signInRequest, e.getMessage(), req, resp);
        }
    }

    private void handleUnauthorized(SignInRequest signInRequest, String error,
                                    HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        WebContext context = buildWebContext(request, response);
        context.setVariable(SIGN_IN_REQUEST_VARIABLE, signInRequest);
        context.setVariable("error", error);
        templateEngine.process(SIGN_IN_PAGE, context, response.getWriter());
    }
}
