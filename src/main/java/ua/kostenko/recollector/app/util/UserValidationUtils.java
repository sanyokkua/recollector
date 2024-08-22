package ua.kostenko.recollector.app.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ua.kostenko.recollector.app.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidationUtils {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 16;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public boolean isEmailValid(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public boolean isPasswordValid(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }
        return password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH;
    }

    public List<String> validateUser(UserDto user) {
        if (Objects.isNull(user)) {
            return List.of("User is null");
        }
        var errors = new ArrayList<String>();

        if (!isEmailValid(user.getEmail())) {
            errors.add("Email '" + user.getEmail() + "' is not valid");
        }

        if (!isPasswordValid(user.getPassword())) {
            errors.add("Password '" + user.getPassword() + "' is not valid");
        }

        if (!isPasswordValid(user.getPasswordConfirm())) {
            errors.add("Password Confirm '" + user.getPasswordConfirm() + "' is not valid");
        }

        var passwordsAreNotNull = Objects.nonNull(user.getPassword()) && Objects.nonNull(user.getPasswordConfirm());
        if (passwordsAreNotNull && !user.getPassword().equals(user.getPasswordConfirm())) {
            errors.add("Password and Confirm Password do not match");
        }
        return errors;
    }
}
