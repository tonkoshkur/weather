package ua.tonkoshkur.weather.session;

public class ExpiredSessionException extends RuntimeException {
    public ExpiredSessionException() {
        super("Session has expired");
    }
}
