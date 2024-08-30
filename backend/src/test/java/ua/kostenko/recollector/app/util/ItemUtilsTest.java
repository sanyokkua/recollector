package ua.kostenko.recollector.app.util;

import org.junit.jupiter.api.Test;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.exception.CategoryValidationException;
import ua.kostenko.recollector.app.exception.ItemValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemUtilsTest {

    @Test
    void isValidItem_whenItemDtoIsValid_returnsTrue() {
        ItemDto validItemDto = ItemDto.builder()
                                      .categoryId(1L)
                                      .itemName("Test Item")
                                      .itemStatus("Pending")
                                      .itemNotes("Some notes")
                                      .build();

        assertTrue(ItemUtils.isValidItem(validItemDto), "The ItemDto should be valid");
    }

    @Test
    void isValidItem_whenCategoryIdIsNull_returnsFalse() {
        ItemDto invalidItemDto = ItemDto.builder().categoryId(null).itemName("Test Item").itemStatus("Pending").build();

        assertFalse(ItemUtils.isValidItem(invalidItemDto), "The ItemDto should be invalid with null categoryId");
    }

    @Test
    void isValidItem_whenItemNameIsBlank_returnsFalse() {
        ItemDto invalidItemDto = ItemDto.builder().categoryId(1L).itemName("") // blank item name
                                        .itemStatus("Pending").build();

        assertFalse(ItemUtils.isValidItem(invalidItemDto), "The ItemDto should be invalid with blank itemName");
    }

    @Test
    void isValidItem_whenItemStatusIsBlank_returnsFalse() {
        ItemDto invalidItemDto = ItemDto.builder()
                                        .categoryId(1L)
                                        .itemName("Test Item")
                                        .itemStatus("") // blank item status
                                        .build();

        assertFalse(ItemUtils.isValidItem(invalidItemDto), "The ItemDto should be invalid with blank itemStatus");
    }

    @Test
    void isValidItem_whenAllFieldsAreInvalid_returnsFalse() {
        ItemDto invalidItemDto = ItemDto.builder().categoryId(null).itemName("").itemStatus("").build();

        assertFalse(ItemUtils.isValidItem(invalidItemDto), "The ItemDto should be invalid with all invalid fields");
    }

    @Test
    void isValidItem_whenItemDtoIsNull_returnsFalse() {
        assertFalse(ItemUtils.isValidItem(null), "The ItemDto should be invalid when the input is null");
    }

    @Test
    void mapToDto_whenItemIsValid_returnsItemDto() {
        Category category = Category.builder()
                                    .categoryId(1L)
                                    .categoryName("Work")
                                    .build(); // Assuming Category has a builder

        Item item = Item.builder()
                        .itemId(1L)
                        .category(category)
                        .itemName("Test Item")
                        .itemStatus("Pending")
                        .itemNotes("Some notes")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        ItemDto itemDto = ItemUtils.mapToDto(item);

        assertNotNull(itemDto, "ItemDto should not be null");
        assertEquals(item.getItemId(), itemDto.getItemId(), "Item ID should match");
        assertEquals(item.getItemName(), itemDto.getItemName(), "Item Name should match");
        assertEquals(item.getItemStatus(), itemDto.getItemStatus(), "Item Status should match");
        assertEquals(item.getItemNotes(), itemDto.getItemNotes(), "Item Notes should match");
    }

    @Test
    void mapToDto_whenItemIsNull_returnsNull() {
        assertNull(ItemUtils.mapToDto(null), "ItemDto should be null when input Item is null");
    }

    @Test
    void validateItemDto_whenItemDtoIsValid_doesNotThrowException() {
        // Arrange
        ItemDto validItemDto = ItemDto.builder().categoryId(1L).itemName("Test Item").itemStatus("Pending").build();

        // Act & Assert
        assertDoesNotThrow(() -> ItemUtils.validateItemDto(validItemDto),
                           "validateItemDto should not throw an exception for valid ItemDto");
    }

    @Test
    void validateItemDto_whenItemDtoIsInvalid_throwsItemValidationException() {
        // Arrange
        ItemDto invalidItemDto = ItemDto.builder().categoryId(null) // Invalid categoryId
                                        .itemName("") // Invalid itemName
                                        .itemStatus("Pending").build();

        // Act & Assert
        ItemValidationException exception = assertThrows(ItemValidationException.class,
                                                         () -> ItemUtils.validateItemDto(invalidItemDto),
                                                         "validateItemDto should throw ItemValidationException for invalid ItemDto");
        assertEquals("Item is null or has invalid field values", exception.getMessage());
    }

    @Test
    void validateCategoryId_whenCategoryIdIsValid_doesNotThrowException() {
        // Arrange
        Long validCategoryId = 1L;

        // Act & Assert
        assertDoesNotThrow(() -> ItemUtils.validateCategoryId(validCategoryId),
                           "validateCategoryId should not throw an exception for valid categoryId");
    }

    @Test
    void validateCategoryId_whenCategoryIdIsNull_throwsCategoryValidationException() {
        // Arrange
        Long invalidCategoryId = null;

        // Act & Assert
        CategoryValidationException exception = assertThrows(CategoryValidationException.class,
                                                             () -> ItemUtils.validateCategoryId(invalidCategoryId),
                                                             "validateCategoryId should throw CategoryValidationException for null categoryId");
        assertEquals("CategoryId is null", exception.getMessage());
    }

    @Test
    void validateItemId_whenItemIdIsValid_doesNotThrowException() {
        // Arrange
        Long validItemId = 1L;

        // Act & Assert
        assertDoesNotThrow(() -> ItemUtils.validateItemId(validItemId),
                           "validateItemId should not throw an exception for valid itemId");
    }

    @Test
    void validateItemId_whenItemIdIsNull_throwsItemValidationException() {
        // Arrange
        Long invalidItemId = null;

        // Act & Assert
        ItemValidationException exception = assertThrows(ItemValidationException.class,
                                                         () -> ItemUtils.validateItemId(invalidItemId),
                                                         "validateItemId should throw ItemValidationException for null itemId");
        assertEquals("ItemId is null", exception.getMessage());
    }
}
