package ua.kostenko.recollector.app.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.UserSettingsDto;
import ua.kostenko.recollector.app.entity.UserSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for operations related to {@link UserSettings} and {@link UserSettingsDto}.
 * Provides static methods to validate color strings and UserSettingsDto objects, and to map UserSettings entities to UserSettingsDto objects.
 * <p>
 * The constructor is private to prevent instantiation.
 * <p>
 * Logging is enabled using Lombok's {@link Slf4j} annotation for capturing important events and errors.
 */
@Slf4j
public class UserSettingsUtils {

    /**
     * Validates a color string to ensure it meets the format requirements.
     * A valid color string should be non-null, non-blank, start with '#', and have exactly 7 characters.
     *
     * @param color the color string to be validated
     *
     * @return a {@link ValidationResult} indicating whether the color is valid and listing any errors
     */
    public static ValidationResult isValidColor(String color) {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(color)) {
            errors.add("Color is null");
            log.error("Color validation failed: {}", errors.get(0));
            return new ValidationResult(false, errors);
        }
        if (StringUtils.isBlank(color)) {
            errors.add("Color is blank");
        }
        if (!color.startsWith("#")) {
            errors.add("Color must start with '#'");
        }
        if (color.length() != 7) {
            errors.add("Color must have exactly 7 characters");
        }
        boolean isValid = errors.isEmpty();
        log.debug("Color validation result for '{}': {}", color, isValid ? "valid" : errors);
        return new ValidationResult(isValid, errors);
    }

    /**
     * Validates a {@link UserSettingsDto} object to ensure all fields meet the required constraints.
     * Checks that the UserSettingsDto is not null, email is not blank, color values are valid, and page sizes are positive.
     *
     * @param userSettingsDto the {@link UserSettingsDto} object to be validated
     *
     * @return a {@link ValidationResult} indicating whether the settings are valid and listing any errors
     */
    public static ValidationResult isValidSettingsDto(UserSettingsDto userSettingsDto) {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(userSettingsDto)) {
            errors.add("UserSettingsDto is null");
            log.error("UserSettingsDto validation failed: {}", errors.get(0));
            return new ValidationResult(false, errors);
        }
        if (StringUtils.isBlank(userSettingsDto.getUserEmail())) {
            errors.add("UserSettingsDto userEmail is blank");
        }
        ValidationResult colorValidationResult = validateColors(userSettingsDto);
        errors.addAll(colorValidationResult.errors());

        if (Objects.isNull(userSettingsDto.getCategoryPageSize())) {
            errors.add("CategoryPageSize is null");
        }
        if (userSettingsDto.getCategoryPageSize() <= 0) {
            errors.add("CategoryPageSize must be greater than 0");
        }

        if (Objects.isNull(userSettingsDto.getItemPageSize())) {
            errors.add("ItemPageSize is null");
        }
        if (userSettingsDto.getItemPageSize() <= 0) {
            errors.add("ItemPageSize must be greater than 0");
        }

        boolean isValid = errors.isEmpty();
        log.debug("UserSettingsDto validation result for email '{}': {}",
                  userSettingsDto.getUserEmail(),
                  isValid ? "valid" : errors);
        return new ValidationResult(isValid, errors);
    }

    /**
     * Validates color fields within the {@link UserSettingsDto}.
     *
     * @param userSettingsDto the {@link UserSettingsDto} object containing color fields to be validated
     *
     * @return a {@link ValidationResult} indicating whether the color fields are valid and listing any errors
     */
    private static ValidationResult validateColors(UserSettingsDto userSettingsDto) {
        ValidationResult categoryBackgroundColorValidation = isValidColor(userSettingsDto.getCategoryBackgroundColor());
        ValidationResult categoryItemColorValidation = isValidColor(userSettingsDto.getCategoryItemColor());
        ValidationResult categoryFabColorValidation = isValidColor(userSettingsDto.getCategoryFabColor());
        ValidationResult itemBackgroundColorValidation = isValidColor(userSettingsDto.getItemBackgroundColor());
        ValidationResult itemItemColorValidation = isValidColor(userSettingsDto.getItemItemColor());
        ValidationResult itemFabColorValidation = isValidColor(userSettingsDto.getItemFabColor());

        List<String> errors = new ArrayList<>();
        errors.addAll(categoryBackgroundColorValidation.errors());
        errors.addAll(categoryItemColorValidation.errors());
        errors.addAll(categoryFabColorValidation.errors());
        errors.addAll(itemBackgroundColorValidation.errors());
        errors.addAll(itemItemColorValidation.errors());
        errors.addAll(itemFabColorValidation.errors());

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Maps a {@link UserSettings} entity to a {@link UserSettingsDto}.
     *
     * @param userSettings the {@link UserSettings} entity to be mapped
     *
     * @return the corresponding {@link UserSettingsDto}
     */
    public static UserSettingsDto toUserSettingsDto(UserSettings userSettings) {
        UserSettingsDto userSettingsDto = UserSettingsDto.builder()
                                                         .userEmail(userSettings.getUser().getEmail())
                                                         .categoryBackgroundColor(userSettings.getCategoryBackgroundColor())
                                                         .categoryItemColor(userSettings.getCategoryItemColor())
                                                         .categoryFabColor(userSettings.getCategoryFabColor())
                                                         .categoryPageSize(userSettings.getCategoryPageSize())
                                                         .itemBackgroundColor(userSettings.getItemBackgroundColor())
                                                         .itemItemColor(userSettings.getItemItemColor())
                                                         .itemFabColor(userSettings.getItemFabColor())
                                                         .itemPageSize(userSettings.getItemPageSize())
                                                         .build();

        log.debug("Mapped UserSettings to UserSettingsDto: {}", userSettingsDto);
        return userSettingsDto;
    }

    /**
     * A record representing the result of a validation check.
     * Contains a boolean indicating whether the validation was successful and a list of errors.
     */
    public record ValidationResult(boolean isValid, List<String> errors) {}
}
