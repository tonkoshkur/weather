package ua.tonkoshkur.weather.auth.signin;

public record SignInRequest(String login, String password) {
    public SignInRequest() {
        this(null, null);
    }
}
