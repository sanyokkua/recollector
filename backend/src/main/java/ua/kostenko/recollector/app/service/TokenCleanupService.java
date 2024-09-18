package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.repository.InvalidatedTokenRepository;

import java.time.LocalDateTime;

/**
 * Service responsible for cleaning up expired tokens from the database.
 * <p>
 * This service runs a scheduled task to remove tokens that have expired,
 * ensuring that the system does not retain invalid or outdated tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    /**
     * Scheduled task that cleans up expired tokens from the database.
     * <p>
     * This method runs every hour, deleting tokens that have expired
     * based on their expiration date.
     */
    @Scheduled(cron = "0 * * * * *")  // Runs every hour
    public void cleanUpExpiredTokens() {
        log.info("Starting token cleanup process.");
        LocalDateTime now = LocalDateTime.now();

        try {
            invalidatedTokenRepository.deleteByExpiresAtBefore(now);
            log.info("Token cleanup completed. Deleted expired tokens.");
        } catch (Exception e) {
            log.error("An error occurred during the token cleanup process: {}", e.getMessage(), e);
        }
    }
}
