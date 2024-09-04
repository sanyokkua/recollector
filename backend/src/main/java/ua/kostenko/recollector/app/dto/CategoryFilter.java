package ua.kostenko.recollector.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Data Transfer Object for filtering categories.
 * Contains pagination, sorting, and search criteria for categories.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for filtering categories, including pagination and sorting.")
public class CategoryFilter {

    @Schema(description = "Page number for pagination.", example = "0", defaultValue = "0")
    private int page = 0;

    @Schema(description = "Number of categories per page for pagination.", example = "2", defaultValue = "2")
    private int size = 2;

    @Schema(description = "Name of the category to filter by.", example = "Work")
    private String categoryName = "";

    @Schema(description = "Sorting direction for the category list.", example = "ASC", defaultValue = "ASC")
    private Sort.Direction direction = Sort.Direction.ASC;
}