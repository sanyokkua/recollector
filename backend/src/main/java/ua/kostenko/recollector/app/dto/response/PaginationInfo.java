package ua.kostenko.recollector.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for pagination information.
 * Includes details about the current page, items per page, total pages, and sorting information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for pagination information, including current page, items per page, total pages, and sorting details.")
public class PaginationInfo {

    @Schema(description = "The current page number.", example = "1")
    private int currentPage;

    @Schema(description = "The number of items per page.", example = "10")
    private int itemsPerPage;

    @Schema(description = "The total number of pages.", example = "5")
    private int totalPages;

    @Schema(description = "The total number of items across all pages.", example = "47")
    private long totalItems;

    @Schema(description = "The field by which the items are sorted.", example = "name")
    private String sortField;

    @Schema(description = "The direction of sorting, either ascending or descending.", example = "ASC")
    private String sortDirection;
}
