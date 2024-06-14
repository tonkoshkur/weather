package ua.tonkoshkur.weather.location;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.tonkoshkur.weather.common.servlet.BaseServlet;
import ua.tonkoshkur.weather.common.util.PathVariableExtractor;
import ua.tonkoshkur.weather.common.util.UrlBuilder;
import ua.tonkoshkur.weather.user.User;

import java.io.IOException;

@WebServlet(value = {"/location", "/location/*"}, name = "LocationController")
public class LocationController extends BaseServlet {

    private static final String USER_VARIABLE = "user";
    private static final String SEARCH_VARIABLE = "search";
    private final transient HttpRequestToLocationMapper httpRequestToLocationMapper = new HttpRequestToLocationMapper();
    private transient LocationDao locationDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        locationDao = (LocationDao) context.getAttribute(LocationDao.class.getSimpleName());
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Location location = httpRequestToLocationMapper.map(req);
        locationDao.save(location);
        redirectToHome(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int locationId = PathVariableExtractor.extract(req.getPathInfo(), Integer::parseInt);
        User user = (User) req.getSession().getAttribute(USER_VARIABLE);

        locationDao.deleteByIdAndUserId(locationId, user.getId());

        redirectToHome(req, resp);
    }

    private void redirectToHome(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder(req.getContextPath());

        String searchParam = req.getParameter(SEARCH_VARIABLE);
        if (searchParam != null) {
            urlBuilder.addParam(SEARCH_VARIABLE, searchParam);
        }

        resp.sendRedirect(urlBuilder.build());
    }
}
