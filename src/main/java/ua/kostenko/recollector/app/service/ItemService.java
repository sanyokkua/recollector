package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.*;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.util.ItemUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final AuthService authService;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new item.
     *
     * @param userEmail the email of the user creating the item
     * @param itemDto   the item data transfer object
     *
     * @return the created item as a DTO
     */
    public ItemDto createItem(String userEmail, ItemDto itemDto) {
        log.info("Creating item for user: {}", userEmail);

        validateItemDto(itemDto);
        var user = getUser(userEmail);
        var category = validateUserHasCategoryAndGetIt(itemDto.getCategoryId(), user.getUserId());
        validateItemExistenceForName(itemDto.getItemName(), "", category);

        var newItem = buildNewItem(itemDto, category);
        var createdItem = itemRepository.saveAndFlush(newItem);

        log.info("Item created successfully with id: {}", createdItem.getItemId());
        return ItemUtils.mapToDto(createdItem);
    }

    private void validateItemExistenceForName(String newName, String currentName, Category category) {
        var isNewNameAndCurrentTheSame = currentName.equals(newName);
        var exists = itemRepository.existsByItemNameAndCategory_CategoryId(newName, category.getCategoryId());
        if (!isNewNameAndCurrentTheSame && exists) {
            throw new ItemAlreadyExistsException("Item with name '" + newName + "' already exists");
        }
    }

    /**
     * Retrieves all items for a given user and category.
     *
     * @param userEmail  the email of the user
     * @param categoryId the ID of the category
     *
     * @return a list of item DTOs
     */
    public List<ItemDto> getAllItems(String userEmail, Long categoryId) {
        log.info("Retrieving all items for user: {} and categoryId: {}", userEmail, categoryId);

        validateCategoryId(categoryId);
        var user = getUser(userEmail);
        validateUserHasCategoryAndGetIt(categoryId, user.getUserId());

        var items = itemRepository.findAllByCategory_CategoryId(categoryId);

        log.info("Retrieved {} items for categoryId: {}", items.size(), categoryId);
        return items.stream().map(ItemUtils::mapToDto).toList();
    }

    /**
     * Retrieves a specific item by its ID within a category.
     *
     * @param userEmail  the email of the user
     * @param categoryId the ID of the category
     * @param itemId     the ID of the item
     *
     * @return the item as a DTO
     */
    public ItemDto getItem(String userEmail, Long categoryId, Long itemId) {
        log.info("Retrieving item with id: {} for user: {} and categoryId: {}", itemId, userEmail, categoryId);

        validateCategoryId(categoryId);
        validateItemId(itemId);
        var user = getUser(userEmail);
        validateUserHasCategoryAndGetIt(categoryId, user.getUserId());

        var foundItem = itemRepository.findByItemIdAndCategory_CategoryId(itemId, categoryId)
                                      .orElseThrow(() -> new ItemNotFoundException("Item with id '" + itemId + "' not found in category with id '" + categoryId + "'"));

        log.info("Item retrieved successfully with id: {}", itemId);
        return ItemUtils.mapToDto(foundItem);
    }

    /**
     * Updates an existing item.
     *
     * @param userEmail the email of the user
     * @param itemDto   the item data transfer object containing updated information
     *
     * @return the updated item as a DTO
     */
    public ItemDto updateItem(String userEmail, ItemDto itemDto) {
        log.info("Updating item with id: {} for user: {}", itemDto.getItemId(), userEmail);

        validateItemDto(itemDto);
        validateItemId(itemDto.getItemId());

        var user = getUser(userEmail);
        var category = validateUserHasCategoryAndGetIt(itemDto.getCategoryId(), user.getUserId());

        var foundItem = itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(), itemDto.getCategoryId())
                                      .orElseThrow(() -> new ItemNotFoundException("Item with id '" + itemDto.getItemId() + "' not found in category with id '" + itemDto.getCategoryId() + "'"));
        validateItemExistenceForName(itemDto.getItemName(), foundItem.getItemName(), category);

        updateItemDetails(foundItem, itemDto);
        var updatedItem = itemRepository.saveAndFlush(foundItem);

        log.info("Item updated successfully with id: {}", updatedItem.getItemId());
        return ItemUtils.mapToDto(updatedItem);
    }

    /**
     * Deletes an item by its ID within a category.
     *
     * @param userEmail  the email of the user
     * @param categoryId the ID of the category
     * @param itemId     the ID of the item
     *
     * @return a confirmation message
     */
    public String deleteItem(String userEmail, Long categoryId, Long itemId) {
        log.info("Deleting item with id: {} for user: {} and categoryId: {}", itemId, userEmail, categoryId);

        validateCategoryId(categoryId);
        validateItemId(itemId);
        var user = getUser(userEmail);
        validateUserHasCategoryAndGetIt(categoryId, user.getUserId());

        var foundItem = itemRepository.findByItemIdAndCategory_CategoryId(itemId, categoryId);

        if (foundItem.isEmpty()) {
            log.warn("Item with id '{}' not found in category with id '{}'", itemId, categoryId);
            return "Item with id '" + itemId + "' not found in category with id '" + categoryId + "'";
        }

        itemRepository.deleteById(itemId);
        log.info("Item with id '{}' deleted from category with id '{}'", itemId, categoryId);
        return "Item with id '" + itemId + "' deleted from category with id '" + categoryId + "'";
    }

    private void validateItemDto(ItemDto itemDto) {
        if (!ItemUtils.isValidItem(itemDto)) {
            log.error("Invalid itemDto: {}", itemDto);
            throw new ItemValidationException("Item is null or has invalid fields values");
        }
    }

    private void validateCategoryId(Long categoryId) {
        if (Objects.isNull(categoryId)) {
            log.error("CategoryId is null");
            throw new CategoryValidationException("CategoryId is null");
        }
    }

    private void validateItemId(Long itemId) {
        if (Objects.isNull(itemId)) {
            log.error("ItemId is null");
            throw new ItemValidationException("ItemId is null");
        }
    }

    private User getUser(String userEmail) {
        log.debug("Finding user by email: {}", userEmail);
        return authService.findUserByEmail(userEmail);
    }

    private Category validateUserHasCategoryAndGetIt(Long categoryId, Long userId) {
        log.debug("Finding category with id: {} for userId: {}", categoryId, userId);
        var category = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, userId);
        if (category.isEmpty()) {
            log.error("Category with id '{}' not found", categoryId);
            throw new CategoryNotFoundException("Category with id '" + categoryId + "' not found");
        }
        return category.get();
    }

    private Item buildNewItem(ItemDto itemDto, Category category) {
        var status = ItemStatus.valueOf(itemDto.getItemStatus());
        var creationTime = LocalDateTime.now();
        return Item.builder()
                   .category(category)
                   .itemName(itemDto.getItemName())
                   .itemStatus(status.name())
                   .itemNotes(itemDto.getItemNotes())
                   .createdAt(creationTime)
                   .updatedAt(creationTime)
                   .build();
    }

    private void updateItemDetails(Item item, ItemDto itemDto) {
        var status = ItemStatus.valueOf(itemDto.getItemStatus());
        item.setItemName(itemDto.getItemName());
        item.setItemStatus(status.name());
        item.setItemNotes(itemDto.getItemNotes());
        item.setUpdatedAt(LocalDateTime.now());
    }
}
