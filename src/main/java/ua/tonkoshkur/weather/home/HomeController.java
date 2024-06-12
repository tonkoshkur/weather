package ua.tonkoshkur.weather.home;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ua.tonkoshkur.weather.api.WeatherApiClient;
import ua.tonkoshkur.weather.api.WeatherApiException;
import ua.tonkoshkur.weather.api.WeatherDto;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.user.User;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/")
public class HomeController extends BaseServlet {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getSimpleName());
    private static final String USER_VARIABLE = "user";
    private static final String SEARCH_VARIABLE = "search";
    private transient WeatherApiClient weatherApiClient;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        weatherApiClient = (WeatherApiClient) context.getAttribute(WeatherApiClient.class.getSimpleName());
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = buildWebContext(req, resp);

        User user = (User) req.getSession().getAttribute(USER_VARIABLE);
        context.setVariable(USER_VARIABLE, user);

        String search = req.getParameter(SEARCH_VARIABLE);
        context.setVariable(SEARCH_VARIABLE, search);

        if (isSearchValid(search)) {
            handleSearch(search, context);
        }

        templateEngine.process("home", context, resp.getWriter());
    }

    private void handleSearch(String search, WebContext context) {
        try {
            List<WeatherDto> weather = weatherApiClient.findAllByCity(search);
            if (!weather.isEmpty()) {
                context.setVariable("weather", weather);
                return;
            }
        } catch (WeatherApiException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        context.setVariable("error", "Unable to find weather");
    }

    private boolean isSearchValid(String search) {
        return search != null && !search.isBlank();
    }
}
