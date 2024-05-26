package ua.tonkoshkur.weather.common.factory;

import jakarta.servlet.ServletContext;
import lombok.Getter;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@Getter
public class ComponentFactory {

    private final JakartaServletWebApplication application;
    private final WebApplicationTemplateResolver templateResolver;
    private final ITemplateEngine templateEngine;

    public ComponentFactory(ServletContext servletContext) {
        application = JakartaServletWebApplication.buildApplication(servletContext);
        templateResolver = buildTemplateResolver();
        templateEngine = buildTemplateEngine();
    }

    private WebApplicationTemplateResolver buildTemplateResolver() {
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/view/");
        resolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        resolver.setCacheTTLMs(3600000L);
        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        resolver.setCacheable(true);
        return resolver;
    }

    private ITemplateEngine buildTemplateEngine() {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
