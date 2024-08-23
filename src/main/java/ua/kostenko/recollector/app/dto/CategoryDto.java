package ua.kostenko.recollector.app.dto;

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
public class CategoryDto {

    private Long categoryId;
    private String categoryName;
    private Long todoItems;
    private Long inProgressItems;
    private Long finishedItems;
}
