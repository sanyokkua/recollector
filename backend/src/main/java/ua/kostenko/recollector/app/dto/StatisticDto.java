package ua.kostenko.recollector.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user statistics.
 * Includes counts of categories and items in various states.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for user statistics, including counts of categories and items in various states.")
public class StatisticDto {

    @Schema(description = "Total number of categories.", example = "5")
    private long totalNumberOfCategories;

    @Schema(description = "Total number of items.", example = "50")
    private long totalNumberOfItems;

    @Schema(description = "Total number of items marked as 'To Do'.", example = "20")
    private long totalNumberOfItemsTodo;

    @Schema(description = "Total number of items marked as 'In Progress'.", example = "15")
    private long totalNumberOfItemsInProgress;

    @Schema(description = "Total number of items marked as 'Finished'.", example = "10")
    private long totalNumberOfItemsFinished;
}