package ua.kostenko.recollector.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for meta-information related to pagination and other details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for meta-information, including pagination details.")
public class MetaInfo {

    @Schema(description = "Pagination information for the current data set.", implementation = PaginationInfo.class)
    private PaginationInfo pagination;
}