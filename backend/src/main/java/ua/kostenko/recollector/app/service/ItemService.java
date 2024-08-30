package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.ItemFilter;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.specification.ItemSpecification;
import ua.kostenko.recollector.app.exception.CategoryNotFoundException;
import ua.kostenko.recollector.app.exception.ItemAlreadyExistsException;
import ua.kostenko.recollector.app.exception.ItemNotFoundException;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.util.ItemUtils;

import java.time.LocalDateTime;

import static ua.kostenko.recollector.app.util.PageRequestUtils.createPageRequest;

/**
 * Service class for handling operations related to {@link Item}.
 * Provides methods to create, retrieve, update, and delete items,
 * as well as filtering items based on various criteria.
 * <p>
 * Logging is enabled using Lombok's @Slf4j annotation for capturing
 * important events and debugging information.
 */
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
     * @param itemDto   the item data transfer object containing item details
     *
     * @return the created item as a DTO
     */
    public ItemDto createItem(String userEmail, ItemDto itemDto) {
        log.info("Creating item for user: {}", userEmail);

        ItemUtils.validateItemDto(itemDto);
        User user = getUser(userEmail);
        Category category = validateUserHasCategoryAndGetIt(itemDto.getCategoryId(), user.getUserId());
        validateItemExistenceForName(itemDto.getItemName(), "", category);

        Item newItem = buildNewItem(itemDto, category);
        Item createdItem = itemRepository.saveAndFlush(newItem);

        log.info("Item created successfully with id: {}", createdItem.getItemId());
        return ItemUtils.mapToDto(createdItem);
    }

    /**
     * Validates that an item with the given name does not already exist in the specified category.
     *
     * @param newName     the new item name
     * @param currentName the current item name
     * @param category    the category where the item is to be created
     *
     * @throws ItemAlreadyExistsException if an item with the new name already exists in the category
     */
    private void validateItemExistenceForName(String newName, String currentName, Category category) {
        if (!currentName.equals(newName) && itemRepository.existsByItemNameAndCategory_CategoryId(newName,
                                                                                                  category.getCategoryId())) {
            throw new ItemAlreadyExistsException("Item with name '" + newName + "' already exists");
        }
    }

    /**
     * Retrieves items based on filters and pagination.
     *
     * @param userEmail  the email of the user
     * @param categoryId the ID of the category
     * @param itemFilter the filter criteria and pagination information
     *
     * @return a page of item DTOs
     */
    public Page<ItemDto> getItemsByFilters(String userEmail, Long categoryId, ItemFilter itemFilter) {
        ItemUtils.validateCategoryId(categoryId);
        User user = getUser(userEmail);
        validateUserHasCategoryAndGetIt(categoryId, user.getUserId());

        var pageable = createPageRequest(itemFilter.getPage(),
                                         itemFilter.getSize(),
                                         Sort.by(itemFilter.getDirection(), "itemName"));

        var spec = ItemSpecification.builder()
                                    .userId(user.getUserId())
                                    .categoryId(itemFilter.getCategoryId())
                                    .itemName(itemFilter.getItemName())
                                    .itemStatus(itemFilter.getItemStatus())
                                    .build();

        Page<Item> resultFromDb = itemRepository.findAll(spec, pageable);

        log.info("Retrieved {} items for categoryId: {} with filters: {}",
                 resultFromDb.getTotalElements(),
                 categoryId,
                 itemFilter);
        return resultFromDb.map(ItemUtils::mapToDto);
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

        ItemUtils.validateCategoryId(categoryId);
        ItemUtils.validateItemId(itemId);
        User user = getUser(userEmail);
        validateUserHasCategoryAndGetIt(categoryId, user.getUserId());

        Item foundItem = itemRepository.findByItemIdAndCategory_CategoryId(itemId, categoryId)
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

        ItemUtils.validateItemDto(itemDto);
        ItemUtils.validateItemId(itemDto.getItemId());

        User user = getUser(userEmail);
        Category category = validateUserHasCategoryAndGetIt(itemDto.getCategoryId(), user.getUserId());

        Item foundItem = itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(), itemDto.getCategoryId())
                                       .orElseThrow(() -> new ItemNotFoundException("Item with id '" + itemDto.getItemId() + "' not found in category with id '" + itemDto.getCategoryId() + "'"));

        validateItemExistenceForName(itemDto.getItemName(), foundItem.getItemName(), category);

        updateItemDetails(foundItem, itemDto);
        Item updatedItem = itemRepository.saveAndFlush(foundItem);

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

        ItemUtils.validateCategoryId(categoryId);
        ItemUtils.validateItemId(itemId);
        User user = getUser(userEmail);
        validateUserHasCategoryAndGetIt(categoryId, user.getUserId());

        if (itemRepository.findByItemIdAndCategory_CategoryId(itemId, categoryId).isEmpty()) {
            log.warn("Item with id '{}' not found in category with id '{}'", itemId, categoryId);
            return "Item with id '" + itemId + "' not found in category with id '" + categoryId + "'";
        }

        itemRepository.deleteById(itemId);
        log.info("Item with id '{}' deleted from category with id '{}'", itemId, categoryId);
        return "Item with id '" + itemId + "' deleted from category with id '" + categoryId + "'";
    }

    /**
     * Retrieves a user by their email.
     *
     * @param userEmail the email of the user
     *
     * @return the user entity
     */
    private User getUser(String userEmail) {
        log.debug("Finding user by email: {}", userEmail);
        return authService.findUserByEmail(userEmail);
    }

    /**
     * Retrieves a category by its ID for a specific user and validates its existence.
     *
     * @param categoryId the ID of the category
     * @param userId     the ID of the user
     *
     * @return the category entity
     *
     * @throws CategoryNotFoundException if the category is not found for the user
     */
    private Category validateUserHasCategoryAndGetIt(Long categoryId, Long userId) {
        log.debug("Finding category with id: {} for userId: {}", categoryId, userId);
        var category = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, userId);
        if (category.isEmpty()) {
            log.error("Category with id '{}' not found", categoryId);
            throw new CategoryNotFoundException("Category with id '" + categoryId + "' not found");
        }
        return category.get();
    }

    /**
     * Builds a new {@link Item} entity from the given DTO and category.
     *
     * @param itemDto  the item data transfer object
     * @param category the category to associate with the item
     *
     * @return the new {@link Item} entity
     */
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

    /**
     * Updates the details of an existing item with the provided DTO.
     *
     * @param item    the item entity to update
     * @param itemDto the item data transfer object containing updated information
     */
    private void updateItemDetails(Item item, ItemDto itemDto) {
        var status = ItemStatus.valueOf(itemDto.getItemStatus());
        item.setItemName(itemDto.getItemName());
        item.setItemStatus(status.name());
        item.setItemNotes(itemDto.getItemNotes());
        item.setUpdatedAt(LocalDateTime.now());
    }
}
