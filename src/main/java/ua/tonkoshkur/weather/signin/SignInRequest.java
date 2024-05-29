package ua.tonkoshkur.weather.signin;

public record SignInRequest(String login, String password) {
    public SignInRequest() {
        this(null, null);
    }
}
