package ua.kostenko.recollector.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDto {

    private long totalNumberOfCategories;
    private long totalNumberOfItems;
    private long totalNumberOfItemsTodo;
    private long totalNumberOfItemsInProgress;
    private long totalNumberOfItemsFinished;
}
