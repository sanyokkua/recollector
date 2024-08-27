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

@Slf4j
@Service
@RequiredArgsConstructor
public class HelperService {

    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public List<String> getItemStatuses() {
        return Arrays.stream(ItemStatus.values()).map(ItemStatus::name).toList();
    }

    public StatisticDto getStatistics(String userEmail) {
        var user = authService.findUserByEmail(userEmail);
        var numberOfCategories = categoryRepository.countByUser_UserId(user.getUserId());
        var numberOfAllItems = itemRepository.countAllItemsByUserId(user.getUserId());
        var numberOfAllItemsTodo = itemRepository.countAllItemsByUserIdAndStatus(user.getUserId(),
                                                                                 ItemStatus.TODO_LATER.name());
        var numberOfAllItemsInProgress = itemRepository.countAllItemsByUserIdAndStatus(user.getUserId(),
                                                                                       ItemStatus.IN_PROGRESS.name());
        var numberOfAllItemsFinished = itemRepository.countAllItemsByUserIdAndStatus(user.getUserId(),
                                                                                     ItemStatus.FINISHED.name());

        return StatisticDto.builder()
                           .totalNumberOfCategories(numberOfCategories)
                           .totalNumberOfItems(numberOfAllItems)
                           .totalNumberOfItemsTodo(numberOfAllItemsTodo)
                           .totalNumberOfItemsInProgress(numberOfAllItemsInProgress)
                           .totalNumberOfItemsFinished(numberOfAllItemsFinished)
                           .build();
    }
}
