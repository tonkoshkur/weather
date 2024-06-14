package ua.tonkoshkur.weather.location;

import jakarta.servlet.http.HttpServletRequest;
import ua.tonkoshkur.weather.user.User;

import java.math.BigDecimal;

public class HttpRequestToLocationMapper {
    public Location map(HttpServletRequest request) {
        return new Location(
                request.getParameter("name"),
                (User) request.getSession().getAttribute("user"),
                new BigDecimal(request.getParameter("latitude")),
                new BigDecimal(request.getParameter("longitude")));
    }
}
