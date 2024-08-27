package ua.kostenko.recollector.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationInfo {

    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
    private Integer itemsPerPage;
}
