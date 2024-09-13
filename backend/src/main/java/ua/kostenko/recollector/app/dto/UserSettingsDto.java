package ua.kostenko.recollector.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kostenko.recollector.app.entity.UserSettings;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for user settings, represents user UI preferences.")
public class UserSettingsDto {

    @Schema(description = "The email address of the user.", example = "user@example.com")
    private String userEmail;
    @Schema(description = "The color of the category view background.", example = "#1de9b6", defaultValue = UserSettings.DEFAULT_CATEGORY_BACKGROUND_COLOR)
    private String categoryBackgroundColor;
    @Schema(description = "The color of the category item background.", example = "#1de9b6", defaultValue = UserSettings.DEFAULT_CATEGORY_ITEM_COLOR)
    private String categoryItemColor;
    @Schema(description = "The color of the category view fab background.", example = "#1de9b6", defaultValue = UserSettings.DEFAULT_CATEGORY_FAB_COLOR)
    private String categoryFabColor;
    @Schema(description = "The size of the page. Number of items in Category view.", example = "10")
    private Integer categoryPageSize;
    @Schema(description = "The color of the item view background.", example = "#1de9b6", defaultValue = UserSettings.DEFAULT_ITEM_BACKGROUND_COLOR)
    private String itemBackgroundColor;
    @Schema(description = "The color of the item background.", example = "#1de9b6", defaultValue = UserSettings.DEFAULT_ITEM_ITEM_COLOR)
    private String itemItemColor;
    @Schema(description = "The color of the item view fab background.", example = "#1de9b6", defaultValue = UserSettings.DEFAULT_ITEM_FAB_COLOR)
    private String itemFabColor;
    @Schema(description = "The size of the page. Number of items in Items view.", example = "10")
    private Integer itemPageSize;

    public static UserSettingsDto getDefault(String userEmail) {
        return UserSettingsDto.builder()
                              .userEmail(userEmail)
                              .categoryBackgroundColor(UserSettings.DEFAULT_CATEGORY_BACKGROUND_COLOR)
                              .categoryItemColor(UserSettings.DEFAULT_CATEGORY_ITEM_COLOR)
                              .categoryFabColor(UserSettings.DEFAULT_CATEGORY_FAB_COLOR)
                              .categoryPageSize(UserSettings.DEFAULT_CATEGORY_PAGE_SIZE)
                              .itemBackgroundColor(UserSettings.DEFAULT_ITEM_BACKGROUND_COLOR)
                              .itemItemColor(UserSettings.DEFAULT_ITEM_ITEM_COLOR)
                              .itemFabColor(UserSettings.DEFAULT_ITEM_FAB_COLOR)
                              .itemPageSize(UserSettings.DEFAULT_ITEM_PAGE_SIZE)
                              .build();
    }
}
