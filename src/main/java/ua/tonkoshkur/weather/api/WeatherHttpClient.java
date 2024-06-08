package ua.tonkoshkur.weather.api;

import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class WeatherHttpClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final JsonMapper jsonMapper = new JsonMapper();

    public <T> T sendRequest(String url, Class<T> responseType) throws WeatherApiException {
        try {
            String json = sendRequest(url);
            return jsonMapper.readValue(json, responseType);
        } catch (IOException e) {
            throw new WeatherApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherApiException(e);
        }
    }

    private String sendRequest(String url) throws IOException, InterruptedException, WeatherApiException {
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (response.statusCode() != SC_OK) {
            String errorMessage = String.format("Unsuccessful response. Url: %s. Body: %s", url, body);
            throw new WeatherApiException(errorMessage);
        }
        return body;
    }
}
