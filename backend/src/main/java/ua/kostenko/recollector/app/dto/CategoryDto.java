package ua.kostenko.recollector.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a category.
 * Contains details about the category including its ID, name, and item counts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object representing a category.")
public class CategoryDto {

    @Schema(description = "Unique identifier for the category.", example = "1")
    private Long categoryId;

    @Schema(description = "Name of the category.", example = "Work")
    private String categoryName;

    @Schema(description = "(Only in response) Number of items in the 'To Do' state for this category.", example = "5")
    private Long todoItems;

    @Schema(description = "(Only in response) Number of items in the 'In Progress' state for this category.", example = "3")
    private Long inProgressItems;

    @Schema(description = "(Only in response) Number of items in the 'Finished' state for this category.", example = "10")
    private Long finishedItems;
}
