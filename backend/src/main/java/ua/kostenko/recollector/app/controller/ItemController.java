package ua.kostenko.recollector.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.ItemFilter;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exception.ItemValidationException;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.service.ItemService;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing items within a specific category.
 * Provides endpoints for creating, retrieving, updating, and deleting items.
 */
@RestController
@RequestMapping("api/v1/categories/{categoryId}/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final AuthService authService;
    private final ItemService itemService;

    /**
     * Creates a new item within the specified category.
     *
     * @param categoryId the ID of the category to which the item belongs
     * @param itemDto    the item data to be created
     *
     * @return a {@link ResponseEntity} with the created item and HTTP status {@code 201 CREATED}
     */
    @PostMapping
    public ResponseEntity<Response<ItemDto>> createItem(@PathVariable("categoryId") Long categoryId,
                                                        @RequestBody ItemDto itemDto) {
        if (Objects.isNull(categoryId) || (Objects.nonNull(itemDto) && !categoryId.equals(itemDto.getCategoryId()))) {
            log.error("Validation failed: Category ID in path is null or does not match the item payload");
            throw new ItemValidationException("Category id cannot be empty or different in path and body");
        }

        var email = authService.getUserEmailFromAuthContext();
        log.info("Creating item for category ID {} by user {}", categoryId, email);
        var dto = itemService.createItem(email, itemDto);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.CREATED);
    }

    /**
     * Retrieves all items within the specified category that match the provided filters.
     *
     * @param categoryId the ID of the category
     * @param itemFilter the filters to apply to the items
     *
     * @return a {@link ResponseEntity} with the list of items and HTTP status {@code 200 OK}
     */
    @GetMapping
    public ResponseEntity<Response<List<ItemDto>>> getAllItems(@PathVariable("categoryId") Long categoryId,
                                                               ItemFilter itemFilter) {
        var email = authService.getUserEmailFromAuthContext();
        log.info("Fetching items for category ID {} with filters by user {}", categoryId, email);
        var dto = itemService.getItemsByFilters(email, categoryId, itemFilter);
        return ResponseHelper.buildPageDtoResponse(dto, HttpStatus.OK);
    }

    /**
     * Retrieves a specific item by its ID within the specified category.
     *
     * @param categoryId the ID of the category
     * @param itemId     the ID of the item
     *
     * @return a {@link ResponseEntity} with the requested item and HTTP status {@code 200 OK}
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Response<ItemDto>> getItem(@PathVariable("categoryId") Long categoryId,
                                                     @PathVariable("itemId") Long itemId) {
        var email = authService.getUserEmailFromAuthContext();
        log.info("Fetching item ID {} for category ID {} by user {}", itemId, categoryId, email);
        var dto = itemService.getItem(email, categoryId, itemId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }

    /**
     * Updates an existing item within the specified category.
     *
     * @param categoryId the ID of the category
     * @param itemId     the ID of the item to update
     * @param itemDto    the updated item data
     *
     * @return a {@link ResponseEntity} with the updated item and HTTP status {@code 202 ACCEPTED}
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<Response<ItemDto>> updateItem(@PathVariable("categoryId") Long categoryId,
                                                        @PathVariable("itemId") Long itemId,
                                                        @RequestBody ItemDto itemDto) {
        if (Objects.isNull(itemDto)) {
            log.error("Validation failed: Item payload is null");
            throw new ItemValidationException("Item cannot be null");
        }
        if (!categoryId.equals(itemDto.getCategoryId()) || !itemId.equals(itemDto.getItemId())) {
            log.error("Validation failed: Path categoryId and itemId do not match the values in item payload");
            throw new ItemValidationException(
                    "Path categoryId and Path itemId should be equal to values in item payload");
        }

        var email = authService.getUserEmailFromAuthContext();
        log.info("Updating item ID {} for category ID {} by user {}", itemId, categoryId, email);
        var dto = itemService.updateItem(email, itemDto);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.ACCEPTED);
    }

    /**
     * Deletes a specific item by its ID within the specified category.
     *
     * @param categoryId the ID of the category
     * @param itemId     the ID of the item to delete
     *
     * @return a {@link ResponseEntity} with a confirmation message and HTTP status {@code 200 OK}
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Response<String>> deleteItem(@PathVariable("categoryId") Long categoryId,
                                                       @PathVariable("itemId") Long itemId) {
        var email = authService.getUserEmailFromAuthContext();
        log.info("Deleting item ID {} for category ID {} by user {}", itemId, categoryId, email);
        var dto = itemService.deleteItem(email, categoryId, itemId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }
}
