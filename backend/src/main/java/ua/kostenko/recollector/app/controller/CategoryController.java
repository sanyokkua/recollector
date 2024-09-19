package ua.kostenko.recollector.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.CategoryFilter;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exception.CategoryValidationException;
import ua.kostenko.recollector.app.security.AuthenticationService;
import ua.kostenko.recollector.app.service.CategoryService;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing categories.
 * Provides endpoints for creating, retrieving, updating, and deleting categories.
 */
@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "Endpoints for managing categories including creation, retrieval, updating, and deletion.")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthenticationService authService;

    /**
     * Creates a new category.
     *
     * @param category the category details to be created.
     *
     * @return a {@link ResponseEntity} with the created category details and HTTP status {@code 201 Created}.
     */
    @Operation(summary = "Create a new category", description = "Creates a new category with the provided details.")
    @PostMapping
    public ResponseEntity<Response<CategoryDto>> createCategory(
            @RequestBody @Parameter(description = "Details of the category to be created") CategoryDto category) {
        var email = authService.getUserFromAuthContext();
        log.info("Creating category for user with email: {}", email);
        var dto = categoryService.createCategory(email.getEmail(), category);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.CREATED);
    }

    /**
     * Retrieves all categories for the authenticated user, optionally filtered by certain criteria.
     *
     * @param categoryFilter the filter criteria for categories.
     *
     * @return a {@link ResponseEntity} with a list of categories matching the filter criteria and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Retrieve all categories", description = "Retrieves all categories for the authenticated user, with optional filtering by criteria.")
    @GetMapping
    public ResponseEntity<Response<List<CategoryDto>>> getAllCategories(
            @Parameter(description = "Filter criteria for categories") CategoryFilter categoryFilter) {
        var email = authService.getUserFromAuthContext();
        log.info("Retrieving categories for user with email: {}", email);
        var dto = categoryService.getCategoriesByFilters(email.getEmail(), categoryFilter);
        return ResponseHelper.buildPageDtoResponse(dto, HttpStatus.OK);
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param categoryId the ID of the category to be retrieved.
     *
     * @return a {@link ResponseEntity} with the category details and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Retrieve a specific category", description = "Retrieves the category with the specified ID.")
    @GetMapping("/{category_id}")
    public ResponseEntity<Response<CategoryDto>> getCategory(
            @PathVariable("category_id") @Parameter(description = "ID of the category to retrieve") Long categoryId) {
        var email = authService.getUserFromAuthContext();
        log.info("Retrieving category with ID: {} for user with email: {}", categoryId, email);
        var dto = categoryService.getCategory(email.getEmail(), categoryId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }

    /**
     * Updates an existing category.
     *
     * @param categoryId the ID of the category to be updated.
     * @param category   the updated category details.
     *
     * @return a {@link ResponseEntity} with the updated category details and HTTP status {@code 202 Accepted}.
     *
     * @throws CategoryValidationException if the category ID in the path and the payload do not match.
     */
    @Operation(summary = "Update an existing category", description = "Updates the category with the specified ID using the provided details.")
    @PutMapping("/{category_id}")
    public ResponseEntity<Response<CategoryDto>> updateCategory(
            @PathVariable("category_id") @Parameter(description = "ID of the category to be updated") Long categoryId,
            @RequestBody @Parameter(description = "Updated category details") CategoryDto category) {
        var email = authService.getUserFromAuthContext();
        log.info("Updating category with ID: {} for user with email: {}", categoryId, email);

        if (Objects.nonNull(categoryId) && !categoryId.equals(category.getCategoryId())) {
            log.error("Category ID in path ({}) does not match ID in payload ({})",
                      categoryId,
                      category.getCategoryId());
            throw new CategoryValidationException("Category id in path and payload mismatch");
        }

        var dto = categoryService.updateCategory(email.getEmail(), category);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.ACCEPTED);
    }

    /**
     * Deletes a specific category by its ID.
     *
     * @param categoryId the ID of the category to be deleted.
     *
     * @return a {@link ResponseEntity} with a message indicating the result of the deletion and HTTP status {@code 200 OK}.
     */
    @Operation(summary = "Delete a specific category", description = "Deletes the category with the specified ID.")
    @DeleteMapping("/{category_id}")
    public ResponseEntity<Response<String>> deleteCategory(
            @PathVariable("category_id") @Parameter(description = "ID of the category to be deleted") Long categoryId) {
        var email = authService.getUserFromAuthContext();
        log.info("Deleting category with ID: {} for user with email: {}", categoryId, email);
        var dto = categoryService.deleteCategory(email.getEmail(), categoryId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }
}
