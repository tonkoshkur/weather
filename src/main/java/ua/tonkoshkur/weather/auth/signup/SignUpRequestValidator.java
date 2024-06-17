package ua.tonkoshkur.weather.auth.signup;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.common.exception.BadRequestException;

@RequiredArgsConstructor
public class SignUpRequestValidator {

    private static final String LOGIN_REGEX = "^[a-zA-Z0-9_/\\-@.]{3,25}$";

    public void validate(SignUpRequest request) throws BadRequestException {
        validateLogin(request.login());
        checkPasswordsEquals(request.password(), request.confirmPassword());
    }

    private void validateLogin(String login) throws BadRequestException {
        if (!login.matches(LOGIN_REGEX)) {
            throw new BadRequestException("Invalid login");
        }
    }

    private void checkPasswordsEquals(String password1, String password2) throws BadRequestException {
        if (!password1.equals(password2)) {
            throw new BadRequestException("Passwords do not match");
        }
    }
}
