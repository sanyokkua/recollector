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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.kostenko.recollector.app.TestApplicationContextInitializer;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.specification.ItemSpecification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {TestApplicationContextInitializer.class})
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Creating and saving a test user
        testUser = User.builder().email("testuser@example.com").passwordHash("password123").build();
        userRepository.save(testUser);

        // Creating and saving a test category
        testCategory = Category.builder().categoryName("Test Category").user(testUser).build();
        categoryRepository.save(testCategory);
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(testUser);
    }

    @Test
    void saveItem_validItem_itemSavedSuccessfully() {
        Item item = Item.builder()
                        .itemName("Test Item")
                        .itemStatus(ItemStatus.TODO_LATER.name())
                        .category(testCategory)
                        .build();

        Item savedItem = itemRepository.save(item);

        assertThat(savedItem.getItemId()).isNotNull();
        assertThat(savedItem.getItemName()).isEqualTo("Test Item");
        assertThat(savedItem.getCategory()).isEqualTo(testCategory);
    }

    @Test
    void findByItemIdAndCategoryId_existingItem_itemFound() {
        Item item = Item.builder()
                        .itemName("Test Item")
                        .itemStatus(ItemStatus.TODO_LATER.name())
                        .category(testCategory)
                        .build();

        Item savedItem = itemRepository.save(item);

        Optional<Item> foundItem = itemRepository.findByItemIdAndCategory_CategoryId(savedItem.getItemId(),
                                                                                     testCategory.getCategoryId());

        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getItemName()).isEqualTo("Test Item");
        assertThat(foundItem.get().getCategory()).isEqualTo(testCategory);
    }

    @Test
    void findAllByCategoryId_existingCategory_itemsFound() {
        Item item1 = Item.builder()
                         .itemName("Item 1")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item 2")
                         .itemStatus(ItemStatus.IN_PROGRESS.name())
                         .category(testCategory)
                         .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAllByCategory_CategoryId(testCategory.getCategoryId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getItemName).containsExactlyInAnyOrder("Item 1", "Item 2");
    }

    @Test
    void existsByItemNameAndCategoryId_existingItem_returnTrue() {
        Item item = Item.builder()
                        .itemName("Unique Item")
                        .itemStatus(ItemStatus.TODO_LATER.name())
                        .category(testCategory)
                        .build();

        itemRepository.save(item);

        boolean exists = itemRepository.existsByItemNameAndCategory_CategoryId("Unique Item",
                                                                               testCategory.getCategoryId());

        assertThat(exists).isTrue();
    }

    @Test
    void deleteItem_existingItem_itemDeleted() {
        Item item = Item.builder()
                        .itemName("Delete Item")
                        .itemStatus(ItemStatus.TODO_LATER.name())
                        .category(testCategory)
                        .build();

        Item savedItem = itemRepository.save(item);
        itemRepository.deleteById(savedItem.getItemId());

        Optional<Item> deletedItem = itemRepository.findById(savedItem.getItemId());

        assertThat(deletedItem).isNotPresent();
    }

    @Test
    void updateItem_existingItem_itemUpdated() {
        Item item = Item.builder()
                        .itemName("Original Item Name")
                        .itemStatus(ItemStatus.TODO_LATER.name())
                        .category(testCategory)
                        .build();

        Item savedItem = itemRepository.save(item);
        savedItem.setItemName("Updated Item Name");
        itemRepository.save(savedItem);

        Optional<Item> updatedItem = itemRepository.findById(savedItem.getItemId());

        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getItemName()).isEqualTo("Updated Item Name");
    }

    @Test
    void countItemsByCategoryAndStatus_validStatus_itemCountCorrect() {
        Item item1 = Item.builder()
                         .itemName("Item 1")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item 2")
                         .itemStatus(ItemStatus.FINISHED.name())
                         .category(testCategory)
                         .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Long todoLaterCount = itemRepository.countItemsByCategoryAndStatus(testCategory.getCategoryId(),
                                                                           ItemStatus.TODO_LATER.name());
        Long finishedCount = itemRepository.countItemsByCategoryAndStatus(testCategory.getCategoryId(),
                                                                          ItemStatus.FINISHED.name());

        assertThat(todoLaterCount).isEqualTo(1);
        assertThat(finishedCount).isEqualTo(1);
    }

    @Test
    void countAllItemsByUserId_existingUser_itemCountCorrect() {
        Item item1 = Item.builder()
                         .itemName("Item 1")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item 2")
                         .itemStatus(ItemStatus.FINISHED.name())
                         .category(testCategory)
                         .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Long itemCount = itemRepository.countAllItemsByUserId(testUser.getUserId());

        assertThat(itemCount).isEqualTo(2);
    }

    @Test
    void countAllItemsByUserIdAndStatus_validStatus_itemCountCorrect() {
        Item item1 = Item.builder()
                         .itemName("Item 1")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item 2")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item3 = Item.builder()
                         .itemName("Item 3")
                         .itemStatus(ItemStatus.FINISHED.name())
                         .category(testCategory)
                         .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        Long todoLaterCount = itemRepository.countAllItemsByUserIdAndStatus(testUser.getUserId(),
                                                                            ItemStatus.TODO_LATER.name());
        Long finishedCount = itemRepository.countAllItemsByUserIdAndStatus(testUser.getUserId(),
                                                                           ItemStatus.FINISHED.name());

        assertThat(todoLaterCount).isEqualTo(2);
        assertThat(finishedCount).isEqualTo(1);
    }

    @Test
    void saveItem_nullName_throwException() {
        Item item = Item.builder()
                        .itemName(null)
                        .itemStatus(ItemStatus.TODO_LATER.name())
                        .category(testCategory)
                        .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            itemRepository.save(item);
        });
    }

    @Test
    void saveItem_duplicateNameInSameCategory_throwException() {
        Item item1 = Item.builder()
                         .itemName("Duplicate Item")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();
        itemRepository.save(item1);

        Item item2 = Item.builder()
                         .itemName("Duplicate Item")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            itemRepository.save(item2);
        });
    }

    @Test
    void saveItem_duplicateNameInDifferentCategories_itemsSaved() {
        Category anotherCategory = Category.builder().categoryName("Another Category").user(testUser).build();
        categoryRepository.save(anotherCategory);

        Item item1 = Item.builder()
                         .itemName("Duplicate Item")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();
        itemRepository.save(item1);

        Item item2 = Item.builder()
                         .itemName("Duplicate Item")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(anotherCategory)
                         .build();
        Item savedItem = itemRepository.save(item2);

        assertThat(savedItem.getItemId()).isNotNull();
    }

    @Test
    void deleteItem_nonExistingItem_noExceptionThrown() {
        assertDoesNotThrow(() -> itemRepository.deleteById(999L));
    }

    @Test
    void findAll_specWithUserIdAndCategoryIdAndItemName_filtersCorrectly() {
        // Arrange: Create and save multiple items with different attributes
        Item item1 = Item.builder()
                         .itemName("Item A")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item B")
                         .itemStatus(ItemStatus.IN_PROGRESS.name())
                         .category(testCategory)
                         .build();

        Item item3 = Item.builder()
                         .itemName("Item C")
                         .itemStatus(ItemStatus.FINISHED.name())
                         .category(testCategory)
                         .build();

        itemRepository.saveAll(List.of(item1, item2, item3));

        // Act: Create a specification and execute the query
        var spec = ItemSpecification.builder()
                                    .userId(testUser.getUserId())
                                    .categoryId(testCategory.getCategoryId())
                                    .itemName("Item A")
                                    .build();

        List<Item> items = itemRepository.findAll(spec);

        // Assert: Ensure the correct items are returned
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getItemName()).isEqualTo("Item A");
        assertThat(items.get(0).getCategory()).isEqualTo(testCategory);
    }

    @Test
    void findAll_specWithItemStatus_filtersCorrectly() {
        // Arrange: Create and save multiple items with different statuses
        Item item1 = Item.builder()
                         .itemName("Item A")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item B")
                         .itemStatus(ItemStatus.FINISHED.name())
                         .category(testCategory)
                         .build();

        Item item3 = Item.builder()
                         .itemName("Item C")
                         .itemStatus(ItemStatus.FINISHED.name())
                         .category(testCategory)
                         .build();

        itemRepository.saveAll(List.of(item1, item2, item3));

        // Act: Create a specification and execute the query
        var spec = ItemSpecification.builder()
                                    .userId(testUser.getUserId())
                                    .categoryId(testCategory.getCategoryId())
                                    .itemStatus(ItemStatus.FINISHED.name())
                                    .build();

        List<Item> items = itemRepository.findAll(spec);

        // Assert: Ensure the correct items are returned
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getItemStatus).containsOnly(ItemStatus.FINISHED.name());
    }

    @Test
    void findAll_specWithSorting_sortsCorrectly() {
        // Arrange: Create and save multiple items with different names
        Item item1 = Item.builder()
                         .itemName("Item A")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item2 = Item.builder()
                         .itemName("Item B")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        Item item3 = Item.builder()
                         .itemName("Item C")
                         .itemStatus(ItemStatus.TODO_LATER.name())
                         .category(testCategory)
                         .build();

        itemRepository.saveAll(List.of(item1, item2, item3));

        // Act: Create a pageable with sorting by itemName descending and execute the query
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "itemName"));
        var spec = ItemSpecification.builder()
                                    .userId(testUser.getUserId())
                                    .categoryId(testCategory.getCategoryId())
                                    .build();

        Page<Item> itemsPage = itemRepository.findAll(spec, pageable);

        // Assert: Ensure the items are sorted correctly
        List<Item> items = itemsPage.getContent();
        assertThat(items).hasSize(3);
        assertThat(items.get(0).getItemName()).isEqualTo("Item C");
        assertThat(items.get(1).getItemName()).isEqualTo("Item B");
        assertThat(items.get(2).getItemName()).isEqualTo("Item A");
    }
}