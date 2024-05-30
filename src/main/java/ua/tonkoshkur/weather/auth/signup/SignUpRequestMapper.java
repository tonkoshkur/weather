package ua.tonkoshkur.weather.auth.signup;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SignUpRequestMapper {

    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";
    private static final String CONFIRM_PASSWORD_PARAM = "confirmPassword";

    public SignUpRequest map(HttpServletRequest request) {
        String login = request.getParameter(LOGIN_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);
        String confirmPassword = request.getParameter(CONFIRM_PASSWORD_PARAM);
        return new SignUpRequest(login, password, confirmPassword);
    }
}
