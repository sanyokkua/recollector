package ua.kostenko.recollector.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.CategoryFilter;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.CategoryAlreadyExistsException;
import ua.kostenko.recollector.app.exception.CategoryNotFoundException;
import ua.kostenko.recollector.app.exception.CategoryValidationException;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.security.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private final String userEmail = "test@example.com";
    private final Long categoryId = 1L;

    @Mock
    private AuthService authService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ItemRepository itemRepository;
    private CategoryService categoryService;

    private User user;
    private CategoryDto categoryDto;
    private Category category;
    private Category updatedCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(categoryRepository);
        categoryService = new CategoryService(authService, categoryRepository, itemRepository);

        user = User.builder().userId(1L).build();
        categoryDto = CategoryDto.builder().categoryId(categoryId).categoryName("Work").build();
        category = Category.builder()
                           .categoryId(categoryId)
                           .categoryName("Work")
                           .user(user)
                           .createdAt(LocalDateTime.now())
                           .updatedAt(LocalDateTime.now())
                           .build();
        updatedCategory = Category.builder()
                                  .categoryId(categoryId)
                                  .categoryName("Updated Work")
                                  .user(user)
                                  .createdAt(category.getCreatedAt())
                                  .updatedAt(LocalDateTime.now())
                                  .build();
    }

    @Test
    void createCategory_whenValidCategory_returnsCategoryDto() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(category);

        // Act
        CategoryDto result = categoryService.createCategory(userEmail, categoryDto);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDto.getCategoryId(), result.getCategoryId());
        assertEquals(categoryDto.getCategoryName(), result.getCategoryName());
    }

    @Test
    void createCategory_whenCategoryIsInvalid_throwsException() {
        // Arrange
        CategoryDto invalidCategory = CategoryDto.builder().categoryName("").build();

        // Act & Assert
        assertThrows(CategoryValidationException.class,
                     () -> categoryService.createCategory(userEmail, invalidCategory),
                     "Expected createCategory to throw CategoryValidationException");
    }

    @Test
    void getCategory_whenCategoryExists_returnsCategoryDto() {
        // Arrange
        CategoryDto expectedDto = CategoryDto.builder()
                                             .categoryId(categoryId)
                                             .categoryName("Work")
                                             .todoItems(0L)
                                             .inProgressItems(0L)
                                             .finishedItems(0L)
                                             .build();
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())).thenReturn(Optional.of(
                category));
        // Act
        CategoryDto result = categoryService.getCategory(userEmail, categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getCategoryId(), result.getCategoryId());
        assertEquals(expectedDto.getCategoryName(), result.getCategoryName());
    }

    @Test
    void getCategory_whenCategoryDoesNotExist_throwsException() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId,
                                                               user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class,
                     () -> categoryService.getCategory(userEmail, categoryId),
                     "Expected getCategory to throw CategoryNotFoundException");
    }

    @Test
    void updateCategory_whenValidCategory_returnsUpdatedCategoryDto() {
        // Arrange
        CategoryDto updateDto = CategoryDto.builder().categoryId(categoryId).categoryName("Updated Work").build();
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())).thenReturn(Optional.of(
                category));
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(updatedCategory);

        // Act
        CategoryDto result = categoryService.updateCategory(userEmail, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Work", result.getCategoryName());
    }

    @Test
    void updateCategory_whenCategoryDoesNotExist_throwsException() {
        // Arrange
        CategoryDto updateDto = CategoryDto.builder().categoryId(categoryId).categoryName("Updated Work").build();
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId,
                                                               user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class,
                     () -> categoryService.updateCategory(userEmail, updateDto),
                     "Expected updateCategory to throw CategoryNotFoundException");
    }

    @Test
    void deleteCategory_whenCategoryExists_returnsConfirmationMessage() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())).thenReturn(Optional.of(
                Category.builder().categoryId(categoryId).build()));
        doNothing().when(categoryRepository).deleteById(categoryId);

        // Act
        String result = categoryService.deleteCategory(userEmail, categoryId);

        // Assert
        assertEquals("Category with id '1' deleted", result);
    }

    @Test
    void deleteCategory_whenCategoryDoesNotExist_throwsException() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId,
                                                               user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class,
                     () -> categoryService.deleteCategory(userEmail, categoryId),
                     "Expected deleteCategory to throw CategoryNotFoundException");
    }

    @Test
    void getCategoriesByFilters_whenFiltersApplied_returnsPagedCategoryDto() {
        // Arrange
        CategoryFilter filter = CategoryFilter.builder().categoryName("Work")
                                              .page(0)
                                              .size(10)
                                              .direction(Sort.Direction.ASC)
                                              .build();
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(categoryPage);

        // Act
        Page<CategoryDto> result = categoryService.getCategoriesByFilters(userEmail, filter);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Work", result.getContent().get(0).getCategoryName());
    }

    @Test
    void checkCategoryExists_whenNewNameIsDifferentAndExists_throwsException() {
        // Arrange
        CategoryDto updateDto = CategoryDto.builder().categoryId(categoryId).categoryName("Work1").build();
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())).thenReturn(Optional.of(
                category));
        when(categoryRepository.existsByCategoryNameAndUser_UserId("Work1", user.getUserId())).thenReturn(true);

        // Act & Assert
        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class,
                                                                () -> categoryService.updateCategory(userEmail,
                                                                                                     updateDto),
                                                                "Expected checkCategoryExists to throw CategoryAlreadyExistsException");
        assertEquals("Category 'Work1' already exists", exception.getMessage());
    }
}
