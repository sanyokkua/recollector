package ua.kostenko.recollector.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.UserNotFoundException;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.repository.UserRepository;
import ua.kostenko.recollector.app.repository.UserSettingsRepository;
import ua.kostenko.recollector.app.security.AuthenticationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelperServiceTest {

    @Mock
    private AuthenticationService authService;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserSettingsRepository userSettingsRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private HelperService helperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(authService, categoryRepository, itemRepository);
        helperService = new HelperService(authService,
                                          categoryRepository,
                                          itemRepository,
                                          userRepository,
                                          userSettingsRepository);
    }

    @Test
    void getItemStatuses_noParameters_returnsAllItemStatuses() {
        // Act
        List<String> statuses = helperService.getItemStatuses();

        // Assert
        assertNotNull(statuses, "Statuses list should not be null");
        assertEquals(3, statuses.size(), "There should be 3 item statuses");
        assertTrue(statuses.containsAll(List.of("TODO_LATER", "IN_PROGRESS", "FINISHED")),
                   "Statuses should contain all expected values");
    }

    @Test
    void getStatistics_validUserEmail_returnsStatisticsDto() {
        // Arrange
        var userEmail = "test@example.com";
        var userId = 1L;
        User user = User.builder().userId(userId).email(userEmail).build();

        when(authService.findUserByEmail(userEmail)).thenReturn(user);
        when(categoryRepository.countByUser_UserId(userId)).thenReturn(5L);
        when(itemRepository.countAllItemsByUserId(userId)).thenReturn(20L);
        when(itemRepository.countAllItemsByUserIdAndStatus(userId, ItemStatus.TODO_LATER.name())).thenReturn(10L);
        when(itemRepository.countAllItemsByUserIdAndStatus(userId, ItemStatus.IN_PROGRESS.name())).thenReturn(7L);
        when(itemRepository.countAllItemsByUserIdAndStatus(userId, ItemStatus.FINISHED.name())).thenReturn(3L);

        // Act
        StatisticDto statistics = helperService.getStatistics(userEmail);

        // Assert
        assertNotNull(statistics, "Statistics should not be null");
        assertEquals(5L, statistics.getTotalNumberOfCategories(), "Total number of categories should be 5");
        assertEquals(20L, statistics.getTotalNumberOfItems(), "Total number of items should be 20");
        assertEquals(10L, statistics.getTotalNumberOfItemsTodo(), "Total number of TODO_LATER items should be 10");
        assertEquals(7L, statistics.getTotalNumberOfItemsInProgress(), "Total number of IN_PROGRESS items should be 7");
        assertEquals(3L, statistics.getTotalNumberOfItemsFinished(), "Total number of FINISHED items should be 3");

        verify(authService).findUserByEmail(userEmail);
        verify(categoryRepository).countByUser_UserId(userId);
        verify(itemRepository).countAllItemsByUserId(userId);
        verify(itemRepository).countAllItemsByUserIdAndStatus(userId, ItemStatus.TODO_LATER.name());
        verify(itemRepository).countAllItemsByUserIdAndStatus(userId, ItemStatus.IN_PROGRESS.name());
        verify(itemRepository).countAllItemsByUserIdAndStatus(userId, ItemStatus.FINISHED.name());
    }

    @Test
    void getStatistics_userNotFound_throwsException() {
        // Arrange
        String userEmail = "nonexistent@example.com";
        when(authService.findUserByEmail(userEmail)).thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                                                    () -> helperService.getStatistics(userEmail));
        assertEquals("User not found", thrown.getMessage(), "Exception message should match");

        verify(authService).findUserByEmail(userEmail);
        verifyNoInteractions(categoryRepository, itemRepository);
    }
}