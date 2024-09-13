package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.CategoryItemCount;
import ua.kostenko.recollector.app.exception.CategoryValidationException;

import java.util.Objects;

/**
 * Utility class for operations related to {@link Category} and {@link CategoryDto}.
 * This class provides static methods to validate CategoryDto objects and map Category entities to CategoryDto objects.
 * <p>
 * The constructor is private to prevent instantiation.
 * <p>
 * Logging is enabled using Lombok's @Slf4j annotation for logging important events and errors.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryUtils {

    /**
     * Validates if the given {@link CategoryDto} object is valid.
     * A valid CategoryDto should not be null and should have a non-blank category name.
     *
     * @param categoryDto the {@link CategoryDto} object to be validated
     *
     * @return true if the CategoryDto is valid, false otherwise
     */
    public static boolean isValidCategory(CategoryDto categoryDto) {
        var isValid = !Objects.isNull(categoryDto) && !StringUtils.isBlank(categoryDto.getCategoryName());
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
        if (Objects.isNull(category)) {
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

    public static CategoryDto mapCategoryItemCountToCategoryDto(CategoryItemCount category) {
        if (Objects.isNull(category)) {
            log.warn("Attempted to map a null CategoryItemCount to CategoryDto.");
            return null;
        }

        CategoryDto categoryDto = CategoryDto.builder()
                                             .categoryId(category.getCategoryId())
                                             .categoryName(category.getCategoryName())
                                             .todoItems(category.getCountTodoLater())
                                             .inProgressItems(category.getCountInProgress())
                                             .finishedItems(category.getCountFinished())
                                             .build();
        log.debug("Mapped Category to CategoryDto: {}", categoryDto);
        return categoryDto;
    }

    /**
     * Validates a CategoryDto object.
     * Ensures that the category name is not null or blank.
     *
     * @param categoryDto the category data transfer object to validate
     *
     * @throws CategoryValidationException if the category is invalid
     */
    public static void validateCategoryDto(CategoryDto categoryDto) {
        if (!CategoryUtils.isValidCategory(categoryDto)) {
            log.error("Invalid categoryDto: {}", categoryDto);
            throw new CategoryValidationException("Category is null or has blank name");
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
        if (Objects.isNull(categoryId)) {
            log.error("CategoryId is null");
            throw new CategoryValidationException("Category id is null");
        }
    }
}
