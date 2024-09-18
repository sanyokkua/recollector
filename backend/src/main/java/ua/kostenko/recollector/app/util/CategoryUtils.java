package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.CategoryItemCount;
import ua.kostenko.recollector.app.exception.CategoryValidationException;

/**
 * Utility class for operations related to {@link Category} and {@link CategoryDto}.
 * <p>
 * This class provides static methods to validate {@link CategoryDto} objects and map {@link Category}
 * entities to {@link CategoryDto} objects.
 * <p>
 * The constructor is private to prevent instantiation.
 * <p>
 * Logging is enabled using Lombok's {@link Slf4j} annotation for logging important events and errors.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryUtils {

    /**
     * Validates if the given {@link CategoryDto} object is valid.
     * A valid {@link CategoryDto} should not be null and should have a non-blank category name.
     *
     * @param categoryDto the {@link CategoryDto} object to be validated
     *
     * @return true if the {@link CategoryDto} is valid, false otherwise
     */
    public static boolean isValidCategory(CategoryDto categoryDto) {
        boolean isValid = categoryDto != null && StringUtils.isNotBlank(categoryDto.getCategoryName());
        log.debug("CategoryDto validation result: {}", isValid);
        return isValid;
    }

    /**
     * Maps the given {@link Category} entity to a {@link CategoryDto}.
     * If the category parameter is null, this method will return null.
     *
     * @param category the {@link Category} entity to be mapped
     *
     * @return the corresponding {@link CategoryDto} or null if the input category is null
     */
    public static CategoryDto mapToDto(Category category) {
        if (category == null) {
            log.warn("Attempted to map a null Category to CategoryDto.");
            return null;
        }

        CategoryDto categoryDto = CategoryDto.builder()
                                             .categoryId(category.getCategoryId())
                                             .categoryName(category.getCategoryName())
                                             .build();
        log.debug("Mapped Category to CategoryDto: {}", categoryDto);
        return categoryDto;
    }

    /**
     * Maps the given {@link CategoryItemCount} entity to a {@link CategoryDto}.
     * If the categoryItemCount parameter is null, this method will return null.
     *
     * @param categoryItemCount the {@link CategoryItemCount} entity to be mapped
     *
     * @return the corresponding {@link CategoryDto} or null if the input categoryItemCount is null
     */
    public static CategoryDto mapCategoryItemCountToCategoryDto(CategoryItemCount categoryItemCount) {
        if (categoryItemCount == null) {
            log.warn("Attempted to map a null CategoryItemCount to CategoryDto.");
            return null;
        }

        CategoryDto categoryDto = CategoryDto.builder()
                                             .categoryId(categoryItemCount.getCategoryId())
                                             .categoryName(categoryItemCount.getCategoryName())
                                             .todoItems(categoryItemCount.getCountTodoLater())
                                             .inProgressItems(categoryItemCount.getCountInProgress())
                                             .finishedItems(categoryItemCount.getCountFinished())
                                             .build();
        log.debug("Mapped CategoryItemCount to CategoryDto: {}", categoryDto);
        return categoryDto;
    }

    /**
     * Validates a {@link CategoryDto} object.
     * Ensures that the category name is not null or blank.
     *
     * @param categoryDto the {@link CategoryDto} to validate
     *
     * @throws CategoryValidationException if the {@link CategoryDto} is invalid
     */
    public static void validateCategoryDto(CategoryDto categoryDto) {
        if (!isValidCategory(categoryDto)) {
            log.error("Invalid CategoryDto: {}", categoryDto);
            throw new CategoryValidationException("CategoryDto is null or has a blank name");
        }
    }

    /**
     * Validates a category ID.
     * Ensures that the category ID is not null.
     *
     * @param categoryId the ID of the category to validate
     *
     * @throws CategoryValidationException if the category ID is null
     */
    public static void validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            log.error("CategoryId is null");
            throw new CategoryValidationException("Category ID cannot be null");
        }
    }
}
