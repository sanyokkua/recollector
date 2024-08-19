package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        // Handle user registration logic
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        // Handle user login logic
        return userRepository.findByEmail(email);
    }

    // Other authentication methods
}
