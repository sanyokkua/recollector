package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.dto.StatisticDto;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.repository.CategoryRepository;
import ua.kostenko.recollector.app.repository.ItemRepository;
import ua.kostenko.recollector.app.security.AuthService;

import java.util.Arrays;
import java.util.List;

/**
 * Service class that provides helper methods for statistics and item status retrieval.
 * <p>
 * This class leverages various repositories and authentication services to fetch and compute
 * statistical data related to items and categories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HelperService {

    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    /**
     * Retrieves all possible item statuses.
     *
     * @return a list of item status names
     */
    public List<String> getItemStatuses() {
        log.debug("Retrieving item statuses");
        List<String> statuses = Arrays.stream(ItemStatus.values()).map(ItemStatus::name).toList();
        log.info("Retrieved {} item statuses", statuses.size());
        return statuses;
    }

    /**
     * Retrieves statistics for a user based on their email.
     *
     * @param userEmail the email of the user for whom to retrieve statistics
     *
     * @return a {@link StatisticDto} containing the statistics
     */
    public StatisticDto getStatistics(String userEmail) {
        log.info("Fetching statistics for user: {}", userEmail);
        var user = authService.findUserByEmail(userEmail);
        var userId = user.getUserId();

        var numberOfCategories = categoryRepository.countByUser_UserId(userId);
        var numberOfAllItems = itemRepository.countAllItemsByUserId(userId);
        var numberOfAllItemsTodo = itemRepository.countAllItemsByUserIdAndStatus(userId, ItemStatus.TODO_LATER.name());
        var numberOfAllItemsInProgress = itemRepository.countAllItemsByUserIdAndStatus(userId,
                                                                                       ItemStatus.IN_PROGRESS.name());
        var numberOfAllItemsFinished = itemRepository.countAllItemsByUserIdAndStatus(userId,
                                                                                     ItemStatus.FINISHED.name());

        StatisticDto statistics = StatisticDto.builder()
                                              .totalNumberOfCategories(numberOfCategories)
                                              .totalNumberOfItems(numberOfAllItems)
                                              .totalNumberOfItemsTodo(numberOfAllItemsTodo)
                                              .totalNumberOfItemsInProgress(numberOfAllItemsInProgress)
                                              .totalNumberOfItemsFinished(numberOfAllItemsFinished)
                                              .build();

        log.info("Statistics for user {}: {}", userEmail, statistics);
        return statistics;
    }
}
