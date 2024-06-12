package ua.tonkoshkur.weather.common.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptor {
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean check(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
