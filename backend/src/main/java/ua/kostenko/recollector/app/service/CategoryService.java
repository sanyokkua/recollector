package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
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
        var user = getUser(userEmail);
        var categoryName = category.getCategoryName();

        checkCategoryExists(categoryName, "", user.getUserId());

        var newCategory = buildNewCategory(category, user);
        var createdCategory = categoryRepository.saveAndFlush(newCategory);

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

        var user = getUser(userEmail);
        List<Category> allCategories = categoryRepository.findAllByUser_UserId(user.getUserId());
        var categoryDtoList = allCategories.stream().map(CategoryUtils::mapToDto).toList();

        for (CategoryDto category : categoryDtoList) {
            updateCategoryWithCounts(category);
        }

        log.info("Retrieved {} categories for user: {}", allCategories.size(), userEmail);
        return categoryDtoList;
    }

    private void updateCategoryWithCounts(CategoryDto category) {
        Long categoryId = category.getCategoryId();
        var todo = itemRepository.countItemsByCategoryAndStatus(categoryId, ItemStatus.TODO_LATER.name());
        var progress = itemRepository.countItemsByCategoryAndStatus(categoryId, ItemStatus.IN_PROGRESS.name());
        var finished = itemRepository.countItemsByCategoryAndStatus(categoryId, ItemStatus.FINISHED.name());
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

        var user = getUser(userEmail);
        var foundCategory = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())
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

        var user = getUser(userEmail);
        var categoryToUpdate = categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(),
                                                                                 user.getUserId())
                                                 .orElseThrow(() -> new CategoryNotFoundException("Category with id '" + category.getCategoryId() + "' not found"));

        checkCategoryExists(category.getCategoryName(), categoryToUpdate.getCategoryName(), user.getUserId());
        updateCategoryDetails(categoryToUpdate, category);
        var updatedCategory = categoryRepository.saveAndFlush(categoryToUpdate);

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
        var user = getUser(userEmail);
        var foundCategory = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId());

        if (foundCategory.isEmpty()) {
            log.warn("Category with id '{}' not found", categoryId);
            return "Category with id '" + categoryId + "' not found";
        }

        categoryRepository.deleteById(categoryId);
        log.info("Category with id '{}' deleted", categoryId);
        return "Category with id '" + categoryId + "' deleted";
    }

    private void validateCategoryDto(CategoryDto categoryDto) {
        if (!CategoryUtils.isValidCategory(categoryDto)) {
            log.error("Invalid categoryDto: {}", categoryDto);
            throw new CategoryValidationException("Category is null or has blank name");
        }
    }

    private void validateCategoryId(Long categoryId) {
        if (Objects.isNull(categoryId)) {
            log.error("CategoryId is null");
            throw new CategoryValidationException("Category id is null");
        }
    }

    private void checkCategoryExists(String newName, String currentName, Long userId) {
        var isCurrentNameAndNewNameSame = newName.equals(currentName);
        var exists = categoryRepository.existsByCategoryNameAndUser_UserId(newName, userId);
        if (!isCurrentNameAndNewNameSame && exists) {
            log.error("Category '{}' already exists for user with id '{}'", newName, userId);
            throw new CategoryAlreadyExistsException("Category '" + newName + "' already exists");
        }
    }

    private User getUser(String userEmail) {
        log.debug("Finding user by email: {}", userEmail);
        return authService.findUserByEmail(userEmail);
    }

    private Category buildNewCategory(CategoryDto categoryDto, User user) {
        var creationTime = LocalDateTime.now();
        return Category.builder()
                       .user(user)
                       .createdAt(creationTime)
                       .updatedAt(creationTime)
                       .categoryName(categoryDto.getCategoryName())
                       .build();
    }

    private void updateCategoryDetails(Category category, CategoryDto categoryDto) {
        category.setCategoryName(categoryDto.getCategoryName());
        category.setUpdatedAt(LocalDateTime.now());
    }
}
