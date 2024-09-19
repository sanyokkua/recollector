package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.exception.CategoryValidationException;
import ua.kostenko.recollector.app.exception.ItemValidationException;

import java.util.Objects;

/**
 * Utility class for operations related to {@link Item} and {@link ItemDto}.
 * This class provides static methods to validate ItemDto objects and map Item entities to ItemDto objects.
 * <p>
 * The constructor is private to prevent instantiation.
 * <p>
 * Logging is enabled using Lombok's @Slf4j annotation for logging important events and errors.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemUtils {

    /**
     * Validates if the given {@link ItemDto} object is valid.
     * A valid ItemDto should not be null and must have a non-null category ID, a non-blank item name, and a non-blank item status.
     *
     * @param itemDto the {@link ItemDto} object to be validated
     *
     * @return true if the ItemDto is valid, false otherwise
     */
    public static boolean isValidItem(ItemDto itemDto) {
        if (Objects.isNull(itemDto)) {
            log.warn("Invalid ItemDto: itemDto is null.");
            return false;
        }

        boolean hasCategoryId = Objects.nonNull(itemDto.getCategoryId());
        boolean hasName = StringUtils.isNotBlank(itemDto.getItemName());
        boolean hasStatus = Objects.nonNull(itemDto.getItemStatus());

        boolean isValid = hasCategoryId && hasName && hasStatus;
        log.debug("ItemDto validation result: {}, Category ID: {}, Name: {}, Status: {}",
                  isValid,
                  itemDto.getCategoryId(),
                  itemDto.getItemName(),
                  itemDto.getItemStatus());

        return isValid;
    }

    /**
     * Maps the given {@link Item} entity to a {@link ItemDto}.
     * If the item parameter is null, this method will return null.
     *
     * @param item the {@link Item} entity to be mapped
     *
     * @return the corresponding {@link ItemDto} or null if the input item is null
     */
    public static ItemDto mapToDto(Item item) {
        if (Objects.isNull(item)) {
            log.warn("Attempted to map a null Item to ItemDto.");
            return null;
        }

        ItemStatus itemStatus = ItemStatus.valueOf(item.getItemStatus());
        ItemDto itemDto = ItemDto.builder()
                                 .itemId(item.getItemId())
                                 .categoryId(item.getCategory().getCategoryId())
                                 .itemName(item.getItemName())
                                 .itemStatus(itemStatus)
                                 .itemNotes(item.getItemNotes())
                                 .build();
        log.debug("Mapped Item to ItemDto: {}", itemDto);
        return itemDto;
    }

    /**
     * Validates that the given item DTO is not null and contains valid data.
     *
     * @param itemDto the item DTO to validate
     *
     * @throws ItemValidationException if the item DTO is null or contains invalid data
     */
    public static void validateItemDto(ItemDto itemDto) {
        if (!ItemUtils.isValidItem(itemDto)) {
            log.error("Invalid itemDto: {}", itemDto);
            throw new ItemValidationException("Item is null or has invalid field values");
        }
    }

    /**
     * Validates that the given category ID is not null.
     *
     * @param categoryId the category ID to validate
     *
     * @throws CategoryValidationException if the category ID is null
     */
    public static void validateCategoryId(Long categoryId) {
        if (Objects.isNull(categoryId)) {
            log.error("CategoryId is null");
            throw new CategoryValidationException("CategoryId is null");
        }
    }

    /**
     * Validates that the given item ID is not null.
     *
     * @param itemId the item ID to validate
     *
     * @throws ItemValidationException if the item ID is null
     */
    public static void validateItemId(Long itemId) {
        if (Objects.isNull(itemId)) {
            log.error("ItemId is null");
            throw new ItemValidationException("ItemId is null");
        }
    }
}
