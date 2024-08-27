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
public class ItemFilter {

    private int page = 0;
    private int size = 2;
    private long categoryId;
    private String itemName;
    private String itemStatus;
    private Sort.Direction direction = Sort.Direction.ASC;
}
