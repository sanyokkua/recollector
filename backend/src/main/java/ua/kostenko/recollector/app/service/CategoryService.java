package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.CategoryFilter;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.CategoryItemCount;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.specification.CategoryItemCountSpecification;
import ua.kostenko.recollector.app.exception.CategoryAlreadyExistsException;
import ua.kostenko.recollector.app.exception.CategoryNotFoundException;
import ua.kostenko.recollector.app.repository.CategoryItemCountRepository;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.security.AuthenticationService;
import ua.kostenko.recollector.app.util.CategoryUtils;

import static ua.kostenko.recollector.app.util.PageRequestUtils.createPageRequest;

/**
 * Service class for managing categories.
 * Provides methods for CRUD operations on categories as well as filtering and retrieving statistics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final AuthenticationService authService;
    private final CategoryRepository categoryRepository;
    private final CategoryItemCountRepository categoryItemCountRepository;

    private static String buildErrorMessage(Long categoryId) {
        return "Category with id '" + categoryId + "' not found";
    }

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

        CategoryUtils.validateCategoryDto(category);
        User user = getUser(userEmail);
        String categoryName = category.getCategoryName();

        checkCategoryExists(categoryName, "", user.getUserId());

        Category newCategory = buildNewCategory(category, user);
        Category createdCategory = categoryRepository.saveAndFlush(newCategory);

        log.info("Category created successfully with id: {}", createdCategory.getCategoryId());
        return CategoryUtils.mapToDto(createdCategory);
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
        CategoryItemCount foundCategory = categoryItemCountRepository.findByCategoryIdAndUserId(categoryId,
                                                                                                user.getUserId())
                                                                     .orElseThrow(() -> new CategoryNotFoundException(
                                                                             buildErrorMessage(categoryId)));

        CategoryDto categoryDto = CategoryUtils.mapCategoryItemCountToCategoryDto(foundCategory);

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

        CategoryUtils.validateCategoryDto(category);
        CategoryUtils.validateCategoryId(category.getCategoryId());

        User user = getUser(userEmail);
        Category categoryToUpdate = categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(),
                                                                                      user.getUserId())
                                                      .orElseThrow(() -> new CategoryNotFoundException(buildErrorMessage(
                                                              category.getCategoryId())));

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

        CategoryUtils.validateCategoryId(categoryId);
        User user = getUser(userEmail);
        var category = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId());

        if (category.isEmpty()) {
            throw new CategoryNotFoundException(buildErrorMessage(categoryId));
        }

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

        var spec = CategoryItemCountSpecification.builder()
                                                 .userId(user.getUserId())
                                                 .categoryName(categoryFilter.getCategoryName())
                                                 .build();

        Page<CategoryItemCount> resultFromDb = categoryItemCountRepository.findAll(spec, pageable);
        Page<CategoryDto> page = resultFromDb.map(CategoryUtils::mapCategoryItemCountToCategoryDto);
        log.info("Retrieved {} categories with filters for user: {}", resultFromDb.getTotalElements(), userEmail);
        return page;
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
        return Category.builder().user(user).categoryName(categoryDto.getCategoryName()).build();
    }

    /**
     * Updates an existing Category entity with the details from the given CategoryDto.
     *
     * @param category    the Category entity to update
     * @param categoryDto the category data transfer object containing updated information
     */
    private void updateCategoryDetails(Category category, CategoryDto categoryDto) {
        category.setCategoryName(categoryDto.getCategoryName());
    }
}
