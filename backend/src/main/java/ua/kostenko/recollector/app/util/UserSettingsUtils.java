package ua.kostenko.recollector.app.util;

import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.UserSettingsDto;
import ua.kostenko.recollector.app.entity.UserSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserSettingsUtils {

    public static ValidationResult isValidColor(String color) {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(color)) {
            errors.add("Color is null");
            return new ValidationResult(false, errors);
        }
        if (StringUtils.isBlank(color)) {
            errors.add("Color is blank");
        }
        if (!color.startsWith("#")) {
            errors.add("Color must start with '#");
        }
        if (color.length() != 7) {
            errors.add("Color must have exactly 7 characters");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isValidSettingsDto(UserSettingsDto userSettingsDto) {
        List<String> errors = new ArrayList<>();
        if (Objects.isNull(userSettingsDto)) {
            errors.add("UserSettingsDto is null");
            return new ValidationResult(false, errors);
        }
        if (StringUtils.isBlank(userSettingsDto.getUserEmail())) {
            errors.add("UserSettingsDto userEmail is blank");
        }
        ValidationResult isCategoryBackgroundColorValid = isValidColor(userSettingsDto.getCategoryBackgroundColor());
        ValidationResult isCategoryItemColorValid = isValidColor(userSettingsDto.getCategoryItemColor());
        ValidationResult isCategoryFabColorValid = isValidColor(userSettingsDto.getCategoryFabColor());
        ValidationResult isItemBackgroundColorValid = isValidColor(userSettingsDto.getItemBackgroundColor());
        ValidationResult isItemItemColorValid = isValidColor(userSettingsDto.getItemItemColor());
        ValidationResult isItemFabColorValid = isValidColor(userSettingsDto.getItemFabColor());

        errors.addAll(isCategoryBackgroundColorValid.errors);
        errors.addAll(isCategoryItemColorValid.errors);
        errors.addAll(isCategoryFabColorValid.errors);
        errors.addAll(isItemBackgroundColorValid.errors);
        errors.addAll(isItemItemColorValid.errors);
        errors.addAll(isItemFabColorValid.errors);

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

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static UserSettingsDto toUserSettingsDto(UserSettings userSettings) {
        return UserSettingsDto.builder()
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
    }

    public record ValidationResult(boolean isValid, List<String> errors) {}
}
