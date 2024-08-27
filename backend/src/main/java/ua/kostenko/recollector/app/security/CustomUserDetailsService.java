package ua.kostenko.recollector.app.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.entity.User;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of {@link UserDetailsService} to load user-specific data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthService userRepository;

    /**
     * Loads the user by their email.
     *
     * @param email the email of the user to be loaded
     *
     * @return UserDetails containing user information
     *
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Attempting to load user by email: {}", email);
        User user = userRepository.findUserByEmail(email);
        log.info("User found: {}", email);

        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(); // No authorities granted
            }

            @Override
            public String getPassword() {
                return user.getPasswordHash();
            }

            @Override
            public String getUsername() {
                return user.getEmail();
            }
        };

    }
}
