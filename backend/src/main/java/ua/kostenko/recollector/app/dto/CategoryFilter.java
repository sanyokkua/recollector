package ua.kostenko.recollector.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFilter {

    private int page = 0;
    private int size = 2;
    private String categoryName = "";
    private Sort.Direction direction = Sort.Direction.ASC;
}
