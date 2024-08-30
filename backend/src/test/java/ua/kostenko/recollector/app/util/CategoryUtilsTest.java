package ua.kostenko.recollector.app.util;

import org.junit.jupiter.api.Test;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.CategoryValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CategoryUtilsTest {

    @Test
    void isValidCategory_whenCategoryIsValid_returnsTrue() {
        CategoryDto validCategory = CategoryDto.builder()
                                               .categoryId(1L)
                                               .categoryName("Work")
                                               .todoItems(10L)
                                               .inProgressItems(5L)
                                               .finishedItems(3L)
                                               .build();

        assertTrue(CategoryUtils.isValidCategory(validCategory), "The category should be valid");
    }

    @Test
    void isValidCategory_whenCategoryIsNull_returnsFalse() {
        assertFalse(CategoryUtils.isValidCategory(null), "The category should not be valid when null");
    }

    @Test
    void isValidCategory_whenCategoryNameIsBlank_returnsFalse() {
        CategoryDto invalidCategory = CategoryDto.builder().categoryId(1L).categoryName("") // blank name
                                                 .todoItems(10L).inProgressItems(5L).finishedItems(3L).build();

        assertFalse(CategoryUtils.isValidCategory(invalidCategory),
                    "The category should not be valid with a blank name");
    }

    @Test
    void isValidCategory_whenCategoryNameIsWhitespace_returnsFalse() {
        CategoryDto invalidCategory = CategoryDto.builder().categoryId(1L).categoryName("   ") // whitespace name
                                                 .todoItems(10L).inProgressItems(5L).finishedItems(3L).build();

        assertFalse(CategoryUtils.isValidCategory(invalidCategory),
                    "The category should not be valid with a whitespace name");
    }

    @Test
    void isValidCategory_whenCategoryNameIsNonBlank_returnsTrue() {
        CategoryDto validCategory = CategoryDto.builder().categoryId(1L).categoryName("Home") // non-blank name
                                               .todoItems(10L).inProgressItems(5L).finishedItems(3L).build();

        assertTrue(CategoryUtils.isValidCategory(validCategory), "The category should be valid with a non-blank name");
    }

    @Test
    void mapToDto_whenCategoryIsValid_returnsCategoryDto() {
        // Arrange
        User user = User.builder().userId(1L).build(); // Assuming User has a userId field and a builder
        Category category = Category.builder()
                                    .categoryId(1L)
                                    .categoryName("Work")
                                    .user(user)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .items(new ArrayList<>()) // Assuming an empty list of items
                                    .build();

        // Act
        CategoryDto categoryDto = CategoryUtils.mapToDto(category);

        // Assert
        assertNotNull(categoryDto, "CategoryDto should not be null");
        assertEquals(category.getCategoryId(), categoryDto.getCategoryId(), "Category ID should match");
        assertEquals(category.getCategoryName(), categoryDto.getCategoryName(), "Category Name should match");
    }

    @Test
    void mapToDto_whenCategoryIsNull_returnsNull() {
        // Act
        CategoryDto categoryDto = CategoryUtils.mapToDto(null);

        // Assert
        assertNull(categoryDto, "CategoryDto should be null when input Category is null");
    }

    @Test
    void validateCategoryDto_whenCategoryIsValid_doesNotThrowException() {
        // Arrange
        CategoryDto validCategory = CategoryDto.builder()
                                               .categoryId(1L)
                                               .categoryName("Work")
                                               .todoItems(10L)
                                               .inProgressItems(5L)
                                               .finishedItems(3L)
                                               .build();

        // Act & Assert
        assertDoesNotThrow(() -> CategoryUtils.validateCategoryDto(validCategory),
                           "Validation should not throw an exception for a valid CategoryDto");
    }

    @Test
    void validateCategoryDto_whenCategoryIsNull_throwsCategoryValidationException() {
        // Act & Assert
        CategoryValidationException thrown = assertThrows(CategoryValidationException.class,
                                                          () -> CategoryUtils.validateCategoryDto(null),
                                                          "Expected validateCategoryDto to throw, but it didn't");
        assertEquals("Category is null or has blank name", thrown.getMessage(), "Exception message should match");
    }

    @Test
    void validateCategoryDto_whenCategoryNameIsBlank_throwsCategoryValidationException() {
        // Arrange
        CategoryDto invalidCategory = CategoryDto.builder().categoryId(1L).categoryName("") // blank name
                                                 .todoItems(10L).inProgressItems(5L).finishedItems(3L).build();

        // Act & Assert
        CategoryValidationException thrown = assertThrows(CategoryValidationException.class,
                                                          () -> CategoryUtils.validateCategoryDto(invalidCategory),
                                                          "Expected validateCategoryDto to throw, but it didn't");
        assertEquals("Category is null or has blank name", thrown.getMessage(), "Exception message should match");
    }

    @Test
    void validateCategoryDto_whenCategoryNameIsWhitespace_throwsCategoryValidationException() {
        // Arrange
        CategoryDto invalidCategory = CategoryDto.builder().categoryId(1L).categoryName("   ") // whitespace name
                                                 .todoItems(10L).inProgressItems(5L).finishedItems(3L).build();

        // Act & Assert
        CategoryValidationException thrown = assertThrows(CategoryValidationException.class,
                                                          () -> CategoryUtils.validateCategoryDto(invalidCategory),
                                                          "Expected validateCategoryDto to throw, but it didn't");
        assertEquals("Category is null or has blank name", thrown.getMessage(), "Exception message should match");
    }

    @Test
    void validateCategoryId_whenCategoryIdIsNotNull_doesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> CategoryUtils.validateCategoryId(1L),
                           "Validation should not throw an exception for a non-null category ID");
    }

    @Test
    void validateCategoryId_whenCategoryIdIsNull_throwsCategoryValidationException() {
        // Act & Assert
        CategoryValidationException thrown = assertThrows(CategoryValidationException.class,
                                                          () -> CategoryUtils.validateCategoryId(null),
                                                          "Expected validateCategoryId to throw, but it didn't");
        assertEquals("Category id is null", thrown.getMessage(), "Exception message should match");
    }
}