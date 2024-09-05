package ua.kostenko.recollector.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kostenko.recollector.app.entity.ItemStatus;

/**
 * Data Transfer Object representing an item.
 * Contains details about the item including its ID, category, name, status, and notes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object representing an item.")
public class ItemDto {

    @Schema(description = "Unique identifier for the item.", example = "1")
    private Long itemId;

    @Schema(description = "Unique identifier for the category to which the item belongs.", example = "10")
    private Long categoryId;

    @Schema(description = "Name of the item.", example = "Buy groceries")
    private String itemName;

    @Schema(description = "Current status of the item.", example = "IN_PROGRESS")
    private ItemStatus itemStatus;

    @Schema(description = "Additional notes or description for the item.", example = "Need to buy fruits and vegetables.")
    private String itemNotes;
}