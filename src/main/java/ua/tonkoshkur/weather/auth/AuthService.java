package ua.tonkoshkur.weather.auth;

import lombok.RequiredArgsConstructor;
import ua.tonkoshkur.weather.auth.signin.InvalidCredentialsException;
import ua.tonkoshkur.weather.common.util.PasswordEncryptor;
import ua.tonkoshkur.weather.session.Session;
import ua.tonkoshkur.weather.session.SessionDao;
import ua.tonkoshkur.weather.user.User;
import ua.tonkoshkur.weather.user.UserAlreadyExistsException;
import ua.tonkoshkur.weather.user.UserDao;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuthService {

    private final long sessionTtlMinutes;
    private final SessionDao sessionDao;
    private final UserDao userDao;
    private final PasswordEncryptor passwordEncryptor = new PasswordEncryptor();

    public Session signIn(String login, String password) throws InvalidCredentialsException {
        User user = userDao.findByLogin(login)
                .orElseThrow(InvalidCredentialsException::new);
        throwIfWrongPassword(user, password);
        return createSessionForUser(user);
    }

    private void throwIfWrongPassword(User user, String password) throws InvalidCredentialsException {
        if (!passwordEncryptor.check(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
    }

    public Session signUp(String login, String password) throws UserAlreadyExistsException {
        throwIfUserExists(login);
        User user = saveUser(login, password);
        return createSessionForUser(user);
    }

    private void throwIfUserExists(String login) throws UserAlreadyExistsException {
        if (userDao.findByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException();
        }
    }

    private User saveUser(String login, String password) throws UserAlreadyExistsException {
        String encryptedPassword = passwordEncryptor.encrypt(password);
        User user = new User(login, encryptedPassword);
        return userDao.save(user);
    }

    private Session createSessionForUser(User user) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(sessionTtlMinutes);
        Session session = new Session(user, expiresAt);
        return sessionDao.save(session);
    }

    public void signOut(String sessionId) {
        sessionDao.deleteById(sessionId);
    }
}
