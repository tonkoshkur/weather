package ua.tonkoshkur.weather.auth.signup;

public record SignUpRequest(String login, String password, String confirmPassword) {
    public SignUpRequest() {
        this(null, null, null);
    }
}
