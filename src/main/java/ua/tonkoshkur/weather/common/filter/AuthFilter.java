package ua.tonkoshkur.weather.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import ua.tonkoshkur.weather.common.exception.InvalidCredentialsException;

import java.io.IOException;

@WebFilter(servletNames = "LocationController")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (!isAuthorized(httpRequest)) {
            throw new InvalidCredentialsException();
        }
        chain.doFilter(request, response);
    }

    private boolean isAuthorized(HttpServletRequest request) {
        return request.getSession().getAttribute("user") != null;
    }
}
