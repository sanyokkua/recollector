package ua.kostenko.recollector.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Data Transfer Object for filtering items.
 * Includes pagination, sorting, and filtering criteria for items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for filtering items, including pagination and sorting.")
public class ItemFilter {

    @Schema(description = "Page number for pagination.", example = "0", defaultValue = "0")
    private int page = 0;

    @Schema(description = "Number of items per page for pagination.", example = "2", defaultValue = "2")
    private int size = 2;

    @Schema(description = "Unique identifier for the category to which the items belong.", example = "10")
    private long categoryId;

    @Schema(description = "Name of the item to filter by.", example = "Buy groceries")
    private String itemName;

    @Schema(description = "Current status of the item to filter by.", example = "In Progress")
    private String itemStatus;

    @Schema(description = "Sorting direction for the item list.", example = "ASC", defaultValue = "ASC")
    private Sort.Direction direction = Sort.Direction.ASC;
}