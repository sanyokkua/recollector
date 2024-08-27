package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.CategoryFilter;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.specification.CategorySpecification;
import ua.kostenko.recollector.app.exception.CategoryAlreadyExistsException;
import ua.kostenko.recollector.app.exception.CategoryNotFoundException;
import ua.kostenko.recollector.app.exception.CategoryValidationException;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.util.CategoryUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ua.kostenko.recollector.app.util.PageRequestUtils.createPageRequest;

/**
 * Service class for managing categories.
 * Provides methods for CRUD operations on categories as well as filtering and retrieving statistics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    /**
     * Creates a new category.
     *
     * @param userEmail the email of the user creating the category
     * @param category  the category data transfer object
     *
     * @return the created category as a DTO
     */
    public CategoryDto createCategory(String userEmail, CategoryDto category) {
        log.info("Creating category for user: {}", userEmail);

        validateCategoryDto(category);
        User user = getUser(userEmail);
        String categoryName = category.getCategoryName();

        checkCategoryExists(categoryName, "", user.getUserId());

        Category newCategory = buildNewCategory(category, user);
        Category createdCategory = categoryRepository.saveAndFlush(newCategory);

        log.info("Category created successfully with id: {}", createdCategory.getCategoryId());
        return CategoryUtils.mapToDto(createdCategory);
    }

    /**
     * Retrieves all categories for a given user.
     *
     * @param userEmail the email of the user
     *
     * @return a list of category DTOs
     */
    public List<CategoryDto> getAllCategories(String userEmail) {
        log.info("Retrieving all categories for user: {}", userEmail);

        User user = getUser(userEmail);
        List<Category> allCategories = categoryRepository.findAllByUser_UserId(user.getUserId());

        List<CategoryDto> categoryDtoList = allCategories.stream().map(CategoryUtils::mapToDto).toList();

        categoryDtoList.forEach(this::updateCategoryWithCounts);

        log.info("Retrieved {} categories for user: {}", allCategories.size(), userEmail);
        return categoryDtoList;
    }

    /**
     * Updates the given CategoryDto with the counts of items in various statuses.
     * <p>
     * This method retrieves the number of items associated with the specified category that are
     * in the TODO_LATER, IN_PROGRESS, and FINISHED statuses, and updates the corresponding
     * fields in the CategoryDto.
     *
     * @param category the CategoryDto to update with item counts
     */
    private void updateCategoryWithCounts(CategoryDto category) {
        Long categoryId = category.getCategoryId();
        long todo = itemRepository.countItemsByCategoryAndStatus(categoryId, ItemStatus.TODO_LATER.name());
        long progress = itemRepository.countItemsByCategoryAndStatus(categoryId, ItemStatus.IN_PROGRESS.name());
        long finished = itemRepository.countItemsByCategoryAndStatus(categoryId, ItemStatus.FINISHED.name());

        category.setTodoItems(todo);
        category.setInProgressItems(progress);
        category.setFinishedItems(finished);
    }

    /**
     * Retrieves a specific category by its ID.
     *
     * @param userEmail  the email of the user
     * @param categoryId the ID of the category
     *
     * @return the category as a DTO
     */
    public CategoryDto getCategory(String userEmail, Long categoryId) {
        log.info("Retrieving category with id: {} for user: {}", categoryId, userEmail);

        User user = getUser(userEmail);
        Category foundCategory = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())
                                                   .orElseThrow(() -> new CategoryNotFoundException("Category with id '" + categoryId + "' not found"));

        CategoryDto categoryDto = CategoryUtils.mapToDto(foundCategory);
        updateCategoryWithCounts(categoryDto);

        log.info("Category retrieved successfully with id: {}", categoryId);
        return categoryDto;
    }

    /**
     * Updates an existing category.
     *
     * @param userEmail the email of the user
     * @param category  the category data transfer object containing updated information
     *
     * @return the updated category as a DTO
     */
    public CategoryDto updateCategory(String userEmail, CategoryDto category) {
        log.info("Updating category with id: {} for user: {}", category.getCategoryId(), userEmail);

        validateCategoryDto(category);
        validateCategoryId(category.getCategoryId());

        User user = getUser(userEmail);
        Category categoryToUpdate = categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(),
                                                                                      user.getUserId())
                                                      .orElseThrow(() -> new CategoryNotFoundException(
                                                              "Category with id '" + category.getCategoryId() + "' not found"));

        checkCategoryExists(category.getCategoryName(), categoryToUpdate.getCategoryName(), user.getUserId());
        updateCategoryDetails(categoryToUpdate, category);
        Category updatedCategory = categoryRepository.saveAndFlush(categoryToUpdate);

        log.info("Category updated successfully with id: {}", updatedCategory.getCategoryId());
        return CategoryUtils.mapToDto(updatedCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param userEmail  the email of the user
     * @param categoryId the ID of the category
     *
     * @return a confirmation message
     */
    public String deleteCategory(String userEmail, Long categoryId) {
        log.info("Deleting category with id: {} for user: {}", categoryId, userEmail);

        validateCategoryId(categoryId);
        User user = getUser(userEmail);
        categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())
                          .orElseThrow(() -> new CategoryNotFoundException("Category with id '" + categoryId + "' not found"));

        categoryRepository.deleteById(categoryId);
        log.info("Category with id '{}' deleted", categoryId);
        return "Category with id '" + categoryId + "' deleted";
    }

    /**
     * Retrieves categories by filters with pagination.
     *
     * @param userEmail      the email of the user
     * @param categoryFilter the filter criteria
     *
     * @return a page of category DTOs
     */
    public Page<CategoryDto> getCategoriesByFilters(String userEmail, CategoryFilter categoryFilter) {
        log.info("Retrieving categories with filters for user: {}", userEmail);

        User user = getUser(userEmail);
        var pageable = createPageRequest(categoryFilter.getPage(),
                                         categoryFilter.getSize(),
                                         Sort.by(categoryFilter.getDirection(), "categoryName"));

        var spec = CategorySpecification.builder()
                                        .userId(user.getUserId())
                                        .categoryName(categoryFilter.getName())
                                        .build();

        Page<Category> resultFromDb = categoryRepository.findAll(spec, pageable);

        log.info("Retrieved {} categories with filters for user: {}", resultFromDb.getTotalElements(), userEmail);
        return resultFromDb.map(CategoryUtils::mapToDto);
    }

    /**
     * Validates a CategoryDto object.
     * Ensures that the category name is not null or blank.
     *
     * @param categoryDto the category data transfer object to validate
     *
     * @throws CategoryValidationException if the category is invalid
     */
    private void validateCategoryDto(CategoryDto categoryDto) {
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
    private void validateCategoryId(Long categoryId) {
        if (Objects.isNull(categoryId)) {
            log.error("CategoryId is null");
            throw new CategoryValidationException("Category id is null");
        }
    }

    /**
     * Checks if a category with the given name already exists for the specified user.
     * If the new category name is different from the current name, an exception is thrown.
     *
     * @param newName     the new category name to check
     * @param currentName the current category name
     * @param userId      the ID of the user
     *
     * @throws CategoryAlreadyExistsException if a category with the new name already exists
     */
    private void checkCategoryExists(String newName, String currentName, Long userId) {
        boolean isCurrentNameAndNewNameSame = newName.equals(currentName);
        boolean exists = categoryRepository.existsByCategoryNameAndUser_UserId(newName, userId);
        if (!isCurrentNameAndNewNameSame && exists) {
            log.error("Category '{}' already exists for user with id '{}'", newName, userId);
            throw new CategoryAlreadyExistsException("Category '" + newName + "' already exists");
        }
    }

    /**
     * Retrieves a User object by the user's email address.
     *
     * @param userEmail the email of the user to retrieve
     *
     * @return the User object associated with the given email
     */
    private User getUser(String userEmail) {
        log.debug("Finding user by email: {}", userEmail);
        return authService.findUserByEmail(userEmail);
    }

    /**
     * Builds a new Category entity from the given CategoryDto and User.
     *
     * @param categoryDto the category data transfer object
     * @param user        the user creating the category
     *
     * @return a new Category entity
     */
    private Category buildNewCategory(CategoryDto categoryDto, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Category.builder()
                       .user(user).createdAt(now).updatedAt(now)
                       .categoryName(categoryDto.getCategoryName())
                       .build();
    }

    /**
     * Updates an existing Category entity with the details from the given CategoryDto.
     *
     * @param category    the Category entity to update
     * @param categoryDto the category data transfer object containing updated information
     */
    private void updateCategoryDetails(Category category, CategoryDto categoryDto) {
        category.setCategoryName(categoryDto.getCategoryName());
        category.setUpdatedAt(LocalDateTime.now());
    }
}
