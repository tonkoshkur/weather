package ua.tonkoshkur.weather.common.util;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class UrlBuilder {

    private final String url;
    private String query;

    public UrlBuilder(String url) {
        this.url = url;
        this.query = "";
    }

    public UrlBuilder addParam(String name, String value) {
        String encodedValue = URLEncoder.encode(value, Charset.defaultCharset());
        return addParamInner(name, encodedValue);
    }

    public UrlBuilder addParam(String name, long value) {
        return addParamInner(name, value);
    }

    public UrlBuilder addParam(String name, BigDecimal value) {
        return addParamInner(name, value);
    }

    private UrlBuilder addParamInner(String name, Object value) {
        String paramSeparator = query.isEmpty() ? "?" : "&";
        query += paramSeparator + name + "=" + value;
        return this;
    }

    public String build() {
        return url + query;
    }
}
