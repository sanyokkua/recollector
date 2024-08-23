package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.entity.Item;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemUtils {

    public static boolean isValidItem(ItemDto itemDto) {
        if (Objects.isNull(itemDto)) {
            return false;
        }
        boolean hasCategoryId = Objects.nonNull(itemDto.getCategoryId());
        boolean hasName = StringUtils.isNotBlank(itemDto.getItemName());
        boolean hasStatus = StringUtils.isNotBlank(itemDto.getItemStatus());
        return hasCategoryId && hasName && hasStatus;
    }

    public static ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                      .itemId(item.getItemId())
                      .itemName(item.getItemName())
                      .itemStatus(item.getItemStatus())
                      .itemNotes(item.getItemNotes())
                      .build();
    }
}
