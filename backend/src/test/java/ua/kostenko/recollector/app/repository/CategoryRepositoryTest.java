package ua.kostenko.recollector.app.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.kostenko.recollector.app.TestApplicationContextInitializer;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.specification.CategorySpecification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {TestApplicationContextInitializer.class})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Creating and saving a test user for the Category tests
        testUser = User.builder().email("testuser@example.com").passwordHash("password123").build();
        userRepository.saveAndFlush(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    void save_validCategory_categoryIsSaved() {
        // Arrange
        String categoryName = "Create Category";
        Category category = Category.builder().categoryName(categoryName).user(testUser).build();

        // Act
        Category savedCategory = categoryRepository.save(category);

        // Assert
        assertThat(savedCategory.getCategoryId()).isNotNull();
        assertThat(savedCategory.getCategoryName()).isEqualTo(categoryName);
        assertThat(savedCategory.getUser()).isEqualTo(testUser);
    }

    @Test
    void save_notValidCategory_categoryIsNotSaved() {
        // Arrange
        Category category = Category.builder().categoryName(null).user(testUser).build();

        // Act
        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(category);
        });
    }

    @Test
    void findByCategoryIdAndUser_UserId_validIds_categoryIsFound() {
        // Arrange
        String categoryName = "Find Category";
        Category category = Category.builder().categoryName(categoryName).user(testUser).build();
        Category savedCategory = categoryRepository.save(category);

        // Act
        Optional<Category> foundCategory = categoryRepository.findByCategoryIdAndUser_UserId(savedCategory.getCategoryId(),
                                                                                             testUser.getUserId());

        // Assert
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getCategoryName()).isEqualTo(categoryName);
        assertThat(foundCategory.get().getUser()).isEqualTo(testUser);
    }

    @Test
    void findAllByUser_UserId_validUserId_allCategoriesForUserAreFound() {
        // Arrange
        Category category1 = Category.builder().categoryName("Category 1").user(testUser).build();
        Category category2 = Category.builder().categoryName("Category 2").user(testUser).build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // Act
        List<Category> categories = categoryRepository.findAllByUser_UserId(testUser.getUserId());

        // Assert
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getCategoryName)
                              .containsExactlyInAnyOrder("Category 1", "Category 2");
    }

    @Test
    void countByUser_UserId_validUserId_correctCategoryCountReturned() {
        // Arrange
        Category category1 = Category.builder().categoryName("Category 1").user(testUser).build();
        Category category2 = Category.builder().categoryName("Category 2").user(testUser).build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // Act
        Long count = categoryRepository.countByUser_UserId(testUser.getUserId());

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsByCategoryNameAndUser_UserId_categoryExists_trueIsReturned() {
        // Arrange
        String categoryName = "Unique Category";
        Category category = Category.builder().categoryName(categoryName).user(testUser).build();
        categoryRepository.save(category);

        // Act
        boolean exists = categoryRepository.existsByCategoryNameAndUser_UserId(categoryName, testUser.getUserId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByCategoryNameAndUser_UserId_categoryDoesNotExist_falseIsReturned() {
        // Act
        boolean exists = categoryRepository.existsByCategoryNameAndUser_UserId("Non Existing Category",
                                                                               testUser.getUserId());

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_validCategoryId_categoryIsDeleted() {
        // Arrange
        Category category = Category.builder().categoryName("Delete Category").user(testUser).build();
        Category savedCategory = categoryRepository.save(category);

        // Act
        categoryRepository.deleteById(savedCategory.getCategoryId());

        // Assert
        Optional<Category> deletedCategory = categoryRepository.findById(savedCategory.getCategoryId());
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    void save_existingCategoryId_categoryIsUpdated() {
        // Arrange
        Category category = Category.builder().categoryName("Original Category Name").user(testUser).build();
        Category savedCategory = categoryRepository.save(category);

        // Act
        savedCategory.setCategoryName("Updated Category Name");
        categoryRepository.save(savedCategory);
        Optional<Category> updatedCategory = categoryRepository.findById(savedCategory.getCategoryId());

        // Assert
        assertThat(updatedCategory).isPresent();
        assertThat(updatedCategory.get().getCategoryName()).isEqualTo("Updated Category Name");
    }

    @Test
    void save_duplicateCategoryNameForUser_throwsDataIntegrityViolationException() {
        // Arrange
        String categoryName = "Duplicate Category";
        Category category1 = Category.builder().categoryName(categoryName).user(testUser).build();
        categoryRepository.save(category1);

        Category category2 = Category.builder().categoryName(categoryName).user(testUser).build();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(category2);
        });
    }

    @Test
    void save_sameCategoryNameDifferentUsers_categoriesAreSavedSuccessfully() {
        // Arrange
        String categoryName = "Same Category";
        User anotherUser = User.builder().email("anotheruser@example.com").passwordHash("password123").build();
        userRepository.save(anotherUser);

        Category category1 = Category.builder().categoryName(categoryName).user(testUser).build();
        Category category2 = Category.builder().categoryName(categoryName).user(anotherUser).build();

        // Act
        Category savedCategory1 = categoryRepository.save(category1);
        Category savedCategory2 = categoryRepository.save(category2);

        // Assert
        assertThat(savedCategory1).isNotNull();
        assertThat(savedCategory2).isNotNull();
        assertThat(savedCategory1.getCategoryName()).isEqualTo(categoryName);
        assertThat(savedCategory2.getCategoryName()).isEqualTo(categoryName);
        assertThat(savedCategory1.getUser()).isEqualTo(testUser);
        assertThat(savedCategory2.getUser()).isEqualTo(anotherUser);
    }

    @Test
    void delete_nonExistingCategoryId_noActionIsPerformed() {
        // Arrange
        Long nonExistingCategoryId = 999L;

        // Act
        categoryRepository.deleteById(nonExistingCategoryId);

        // Assert
        Optional<Category> deletedCategory = categoryRepository.findById(nonExistingCategoryId);
        assertThat(deletedCategory).isEmpty();
    }

    private void saveCategoriesForUser(User user) {
        categoryRepository.save(Category.builder().categoryName("Food").user(user).build());
        categoryRepository.save(Category.builder().categoryName("Books").user(user).build());
        categoryRepository.save(Category.builder().categoryName("Electronics").user(user).build());
    }

    @Test
    void findAllBySpecification_validUserIdAndCategoryName_categoriesAreFiltered() {
        // Arrange
        saveCategoriesForUser(testUser);
        String categoryName = "Books";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("categoryName"));
        Specification<Category> spec = CategorySpecification.builder()
                                                            .userId(testUser.getUserId())
                                                            .categoryName(categoryName)
                                                            .build();

        // Act
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo(categoryName);
    }

    @Test
    void findAllBySpecification_validUserIdWithoutCategoryName_allCategoriesAreReturned() {
        // Arrange
        saveCategoriesForUser(testUser);
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("categoryName"));
        Specification<Category> spec = CategorySpecification.builder()
                                                            .userId(testUser.getUserId())
                                                            .categoryName(null)
                                                            .build();

        // Act
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.getContent().stream().map(Category::getCategoryName)).containsExactlyInAnyOrder("Food",
                                                                                                          "Books",
                                                                                                          "Electronics");
    }

    @Test
    void findAllBySpecification_invalidUserId_noCategoriesAreReturned() {
        // Arrange
        saveCategoriesForUser(testUser);
        Long invalidUserId = -1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("categoryName"));
        Specification<Category> spec = CategorySpecification.builder().userId(invalidUserId).categoryName(null).build();

        // Act
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllBySpecification_validUserIdAndPartialCategoryName_categoriesAreFilteredByPartialName() {
        // Arrange
        saveCategoriesForUser(testUser);
        String categoryNamePart = "oo";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("categoryName"));
        Specification<Category> spec = CategorySpecification.builder()
                                                            .userId(testUser.getUserId())
                                                            .categoryName(categoryNamePart)
                                                            .build();

        // Act
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getContent().stream().map(Category::getCategoryName)).containsExactlyInAnyOrder("Food",
                                                                                                          "Books");
    }

    @Test
    void findAllBySpecification_validUserIdAndEmptyCategoryName_noCategoryIsFiltered() {
        // Arrange
        saveCategoriesForUser(testUser);
        String emptyCategoryName = "";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("categoryName"));
        Specification<Category> spec = CategorySpecification.builder()
                                                            .userId(testUser.getUserId())
                                                            .categoryName(emptyCategoryName)
                                                            .build();

        // Act
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).size().isEqualTo(3);
    }

    @Test
    void findAllBySpecification_validUserIdAndCategoryNameSortedByCategoryName_categoriesAreSorted() {
        // Arrange
        saveCategoriesForUser(testUser);
        String categoryName = "s";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("categoryName")));
        Specification<Category> spec = CategorySpecification.builder()
                                                            .userId(testUser.getUserId())
                                                            .categoryName(categoryName)
                                                            .build();

        // Act
        Page<Category> result = categoryRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).getCategoryName()).isEqualTo("Books");
        assertThat(result.getContent().get(1).getCategoryName()).isEqualTo("Electronics");
    }
}