package ua.tonkoshkur.weather.auth.signin;

import jakarta.servlet.http.HttpServletRequest;

public class SignInRequestMapper {
    public SignInRequest map(HttpServletRequest request) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        return new SignInRequest(login, password);
    }
}
