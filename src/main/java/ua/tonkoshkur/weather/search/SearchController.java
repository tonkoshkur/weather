package ua.tonkoshkur.weather.search;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.api.WeatherApi;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.user.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchController extends BaseServlet {

    private static final String USER_VARIABLE = "user";
    private static final String SEARCH_VARIABLE = "search";
    private static final String WEATHER_VARIABLE = "weather";
    private static final String ERROR_VARIABLE = "error";
    private static final String SEARCH_PAGE = "search";
    private transient WeatherApi weatherApi;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        weatherApi = (WeatherApi) context.getAttribute(WeatherApi.class.getSimpleName());
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);

        setUserContext(req, context);
        handleSearchRequest(req, context);

        templateEngine.process(SEARCH_PAGE, context, resp.getWriter());
    }

    private void setUserContext(HttpServletRequest req, WebContext context) {
        User user = (User) req.getSession().getAttribute(USER_VARIABLE);
        context.setVariable(USER_VARIABLE, user);
    }

    private void handleSearchRequest(HttpServletRequest req, WebContext context) {
        String search = req.getParameter(SEARCH_VARIABLE);
        context.setVariable(SEARCH_VARIABLE, search);

        if (!isSearchValid(search)) {
            return;
        }
        try {
            List<WeatherDto> weather = weatherApi.findAllByCity(search);
            context.setVariable(WEATHER_VARIABLE, weather);
        } catch (WeatherApiException ex) {
            context.setVariable(ERROR_VARIABLE, ex.getMessage());
        }
    }

    private boolean isSearchValid(String search) {
        return search != null && !search.isBlank();
    }
}
