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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.ItemFilter;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.entity.specification.ItemSpecification;
import ua.kostenko.recollector.app.exception.ItemNotFoundException;
import ua.kostenko.recollector.app.exception.ItemValidationException;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.security.AuthService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;
    private ItemService itemService;

    private String userEmail;
    private User user;
    private Category category;
    private ItemDto itemDto;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(itemRepository, categoryRepository);
        itemService = new ItemService(authService, itemRepository, categoryRepository);

        userEmail = "user@example.com";
        user = User.builder().userId(1L).email(userEmail).build();
        category = Category.builder().categoryId(1L).categoryName("Test").build();
        itemDto = ItemDto.builder()
                         .itemId(1L)
                         .categoryId(1L)
                         .itemName("Test Item")
                         .itemStatus(ItemStatus.IN_PROGRESS.name())
                         .build();
        item = Item.builder()
                   .itemId(1L)
                   .itemName("Test Item")
                   .itemStatus(ItemStatus.IN_PROGRESS.name())
                   .category(category)
                   .build();
    }

    @Test
    void createItem_validItemDto_returnsCreatedItemDto() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.existsByItemNameAndCategory_CategoryId(itemDto.getItemName(),
                                                                   category.getCategoryId())).thenReturn(false);
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(item);

        // Act
        ItemDto result = itemService.createItem(userEmail, itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    void createItem_invalidItemDto_throwsItemValidationException() {
        // Arrange
        ItemDto invalidItemDto = ItemDto.builder().categoryId(null).itemName("").itemStatus("PENDING").build();

        // Act & Assert
        assertThrows(ItemValidationException.class, () -> itemService.createItem(userEmail, invalidItemDto));
    }

    @Test
    void getItemsByFilters_validFilters_returnsPagedItemDto() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        ItemFilter itemFilter = ItemFilter.builder()
                                          .categoryId(category.getCategoryId())
                                          .direction(Sort.Direction.ASC)
                                          .itemName(itemDto.getItemName())
                                          .itemStatus(itemDto.getItemStatus())
                                          .page(0)
                                          .size(10)
                                          .build();
        Pageable pageable = Pageable.ofSize(10);
        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.findAll(any(ItemSpecification.class), any(Pageable.class))).thenReturn(page);

        // Act
        Page<ItemDto> result = itemService.getItemsByFilters(userEmail, category.getCategoryId(), itemFilter);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(itemDto, result.getContent().get(0));
    }

    @Test
    void getItem_existingItemId_returnsItemDto() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(),
                                                               category.getCategoryId())).thenReturn(Optional.of(item));

        // Act
        ItemDto result = itemService.getItem(userEmail, category.getCategoryId(), itemDto.getItemId());

        // Assert
        assertNotNull(result);
        assertEquals(itemDto, result);
    }

    @Test
    void getItem_nonExistingItemId_throwsItemNotFoundException() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(),
                                                               category.getCategoryId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class,
                     () -> itemService.getItem(userEmail, category.getCategoryId(), itemDto.getItemId()));
    }

    @Test
    void updateItem_existingItem_updatesAndReturnsItemDto() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(),
                                                               itemDto.getCategoryId())).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(item);

        // Act
        ItemDto result = itemService.updateItem(userEmail, itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    void updateItem_nonExistingItem_throwsItemNotFoundException() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(),
                                                               itemDto.getCategoryId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(userEmail, itemDto));
    }

    @Test
    void deleteItem_existingItemId_returnsConfirmationMessage() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(),
                                                               category.getCategoryId())).thenReturn(Optional.of(item));

        // Act
        String result = itemService.deleteItem(userEmail, category.getCategoryId(), itemDto.getItemId());

        // Assert
        assertEquals("Item with id '" + itemDto.getItemId() + "' deleted from category with id '" + category.getCategoryId() + "'",
                     result);
        verify(itemRepository).deleteById(itemDto.getItemId());
    }

    @Test
    void deleteItem_nonExistingItemId_returnsNotFoundMessage() {
        // Arrange
        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.findByCategoryIdAndUser_UserId(category.getCategoryId(), user.getUserId())).thenReturn(
                Optional.of(category));
        when(itemRepository.findByItemIdAndCategory_CategoryId(itemDto.getItemId(),
                                                               itemDto.getCategoryId())).thenReturn(Optional.empty());

        // Act
        String result = itemService.deleteItem(userEmail, category.getCategoryId(), itemDto.getItemId());

        // Assert
        assertEquals("Item with id '" + itemDto.getItemId() + "' not found in category with id '" + itemDto.getCategoryId() + "'",
                     result);
    }
}
