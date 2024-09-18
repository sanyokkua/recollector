package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.dto.UserSettingsDto;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.UserSettings;
import ua.kostenko.recollector.app.exception.UserSettingsValidationException;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.repository.UserRepository;
import ua.kostenko.recollector.app.repository.UserSettingsRepository;
import ua.kostenko.recollector.app.security.AuthenticationService;
import ua.kostenko.recollector.app.util.UserSettingsUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service class providing helper methods for statistics and item status retrieval.
 * <p>
 * This class utilizes various repositories and authentication services to fetch and compute
 * statistical data related to items and categories, and manage user settings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HelperService {

    private final AuthenticationService authService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    /**
     * Retrieves all possible item statuses.
     *
     * @return a list of item status names
     */
    public List<String> getItemStatuses() {
        log.debug("Retrieving item statuses.");
        List<String> statuses = Arrays.stream(ItemStatus.values()).map(ItemStatus::name).toList();
        log.info("Retrieved {} item statuses.", statuses.size());
        return statuses;
    }

    /**
     * Retrieves statistics for a user based on their email.
     *
     * @param userEmail the email of the user for whom to retrieve statistics
     *
     * @return a {@link StatisticDto} containing the statistics
     */
    public StatisticDto getStatistics(String userEmail) {
        log.info("Fetching statistics for user with email: {}", userEmail);

        // Fetch user by email
        var user = authService.findUserByEmail(userEmail);
        var userId = user.getUserId();

        // Retrieve counts from repositories
        var numberOfCategories = categoryRepository.countByUser_UserId(userId);
        var numberOfAllItems = itemRepository.countAllItemsByUserId(userId);
        var numberOfAllItemsTodo = itemRepository.countAllItemsByUserIdAndStatus(userId, ItemStatus.TODO_LATER.name());
        var numberOfAllItemsInProgress = itemRepository.countAllItemsByUserIdAndStatus(userId,
                                                                                       ItemStatus.IN_PROGRESS.name());
        var numberOfAllItemsFinished = itemRepository.countAllItemsByUserIdAndStatus(userId,
                                                                                     ItemStatus.FINISHED.name());

        // Create and return statistics DTO
        StatisticDto statistics = StatisticDto.builder()
                                              .totalNumberOfCategories(numberOfCategories)
                                              .totalNumberOfItems(numberOfAllItems)
                                              .totalNumberOfItemsTodo(numberOfAllItemsTodo)
                                              .totalNumberOfItemsInProgress(numberOfAllItemsInProgress)
                                              .totalNumberOfItemsFinished(numberOfAllItemsFinished)
                                              .build();

        log.info("Statistics for user {}: {}", userEmail, statistics);
        return statistics;
    }

    /**
     * Retrieves user settings based on the user's email.
     *
     * @param userEmail the email of the user for whom to retrieve settings
     *
     * @return a {@link UserSettingsDto} containing user settings
     */
    public UserSettingsDto getUserSettings(String userEmail) {
        log.info("Fetching settings for user with email: {}", userEmail);

        // Fetch user by email
        var user = authService.findUserByEmail(userEmail);
        var userId = user.getUserId();

        // Retrieve user settings from repository
        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_UserId(userId);

        UserSettingsDto settingsDto = userSettings.map(UserSettingsUtils::toUserSettingsDto)
                                                  .orElse(UserSettingsDto.getDefault(userEmail));

        log.info("Retrieved settings for user {}: {}", userEmail, settingsDto);
        return settingsDto;
    }

    /**
     * Saves or updates user settings based on the user's email and provided settings DTO.
     *
     * @param userEmail       the email of the user for whom to save settings
     * @param userSettingsDto the settings data to be saved
     *
     * @return a {@link UserSettingsDto} containing the saved settings
     *
     * @throws UserSettingsValidationException if validation fails
     */
    public UserSettingsDto saveUserSettings(String userEmail, UserSettingsDto userSettingsDto) {
        log.info("Saving settings for user with email: {}", userEmail);

        // Validate user settings DTO
        var validationResult = UserSettingsUtils.isValidSettingsDto(userSettingsDto);
        if (!validationResult.isValid()) {
            String errMsg = String.join(";", validationResult.errors());
            log.error("User settings validation failed: {}", errMsg);
            throw new UserSettingsValidationException(errMsg);
        }

        // Fetch user and existing settings
        var user = authService.findUserByEmail(userEmail);
        var userId = user.getUserId();
        var userSettings = userSettingsRepository.findByUser_UserId(userId);

        // Create or update settings
        UserSettings settingsToUpdate = userSettings.orElseGet(UserSettings::new);
        settingsToUpdate.setUser(user);
        settingsToUpdate.setCategoryBackgroundColor(userSettingsDto.getCategoryBackgroundColor());
        settingsToUpdate.setCategoryItemColor(userSettingsDto.getCategoryItemColor());
        settingsToUpdate.setCategoryFabColor(userSettingsDto.getCategoryFabColor());
        settingsToUpdate.setCategoryPageSize(userSettingsDto.getCategoryPageSize());
        settingsToUpdate.setItemBackgroundColor(userSettingsDto.getItemBackgroundColor());
        settingsToUpdate.setItemItemColor(userSettingsDto.getItemItemColor());
        settingsToUpdate.setItemFabColor(userSettingsDto.getItemFabColor());
        settingsToUpdate.setItemPageSize(userSettingsDto.getItemPageSize());

        UserSettings savedSettings = userSettingsRepository.saveAndFlush(settingsToUpdate);
        UserSettingsDto savedSettingsDto = UserSettingsUtils.toUserSettingsDto(savedSettings);

        log.info("Saved settings for user {}: {}", userEmail, savedSettingsDto);
        return savedSettingsDto;
    }
}
