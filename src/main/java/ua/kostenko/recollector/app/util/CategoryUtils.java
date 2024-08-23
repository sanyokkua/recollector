package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.entity.Category;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryUtils {

    public static boolean isValidCategory(CategoryDto categoryDto) {
        return !Objects.isNull(categoryDto) && !StringUtils.isBlank(categoryDto.getCategoryName());
    }

    public static CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                          .categoryId(category.getCategoryId())
                          .categoryName(category.getCategoryName())
                          .build();
    }
}
