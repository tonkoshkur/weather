package ua.tonkoshkur.weather.home;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.location.weather.LocationWeatherDto;
import ua.tonkoshkur.weather.location.weather.LocationWeatherService;
import ua.tonkoshkur.weather.user.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/")
public class HomeController extends BaseServlet {

    private static final String USER_VARIABLE = "user";
    private static final String SEARCH_VARIABLE = "search";
    private static final String ERROR_VARIABLE = "error";
    private transient LocationWeatherService locationWeatherService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        locationWeatherService = (LocationWeatherService) context.getAttribute(LocationWeatherService.class.getSimpleName());
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);

        User user = (User) req.getSession().getAttribute(USER_VARIABLE);
        context.setVariable(USER_VARIABLE, user);

        String search = req.getParameter(SEARCH_VARIABLE);
        context.setVariable(SEARCH_VARIABLE, search);

        handleSearch(search, user, context);

        templateEngine.process("home", context, resp.getWriter());
    }

    private void handleSearch(String search, User user, WebContext context) {
        List<LocationWeatherDto> locationWeather = locationWeatherService.findAllByCityAndUser(search, user);
        context.setVariable("locationWeather", locationWeather);
        if (locationWeather.isEmpty()) {
            context.setVariable(ERROR_VARIABLE, "No weather found");
        }
    }
}
