package ua.tonkoshkur.weather.common.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThymeleafUtil {

    public static WebContext buildWebContext(HttpServletRequest request, HttpServletResponse response,
                                             JakartaServletWebApplication application) {
        IServletWebExchange webExchange = application.buildExchange(request, response);
        return new WebContext(webExchange);
    }
}
