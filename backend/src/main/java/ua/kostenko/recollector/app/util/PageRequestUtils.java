package ua.kostenko.recollector.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ua.kostenko.recollector.app.dto.response.MetaInfo;
import ua.kostenko.recollector.app.dto.response.PaginationInfo;

import java.util.Objects;

/**
 * Utility class for handling pagination-related operations.
 * Provides static methods to build MetaInfo from a Page object and to create a Pageable object.
 * <p>
 * The constructor is private to prevent instantiation.
 * <p>
 * Logging is enabled using Lombok's @Slf4j annotation for logging important events.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageRequestUtils {

    /**
     * Builds MetaInfo from a {@link Page} object.
     * Provides metadata including current page, items per page, total pages, total items, and sort information.
     *
     * @param data the {@link Page} object containing pagination data
     * @param <T>  the type of the data contained in the page
     *
     * @return the {@link MetaInfo} object containing pagination metadata
     */
    public static <T> MetaInfo buildMetaInfo(Page<T> data) {
        if (Objects.isNull(data)) {
            return MetaInfo.builder().build();
        }

        var currentPage = data.getNumber() + 1;
        var itemsPerPage = data.getNumberOfElements();
        var totalPages = data.getTotalPages();
        var totalItems = data.getTotalElements();

        var order = data.getSort().get().findFirst();
        var direction = order.map(sortOrder -> sortOrder.getDirection().toString()).orElse("");
        var field = order.map(Sort.Order::getProperty).orElse("");

        var pageInfo = PaginationInfo.builder()
                                     .currentPage(currentPage)
                                     .itemsPerPage(itemsPerPage)
                                     .totalPages(totalPages)
                                     .totalItems(totalItems)
                                     .sortDirection(direction)
                                     .sortField(field)
                                     .build();

        var metaInfo = MetaInfo.builder().pagination(pageInfo).build();
        log.debug("Built MetaInfo: {}", metaInfo);
        return metaInfo;
    }

    /**
     * Creates a {@link Pageable} object with the specified page, size, and sort order.
     * Adjusts the page number to be zero-based index.
     *
     * @param page the page number (1-based index)
     * @param size the number of items per page
     * @param sort the sorting criteria
     *
     * @return the {@link Pageable} object
     */
    public static Pageable createPageRequest(int page, int size, Sort sort) {
        var adjustedPage = Math.max(page - 1, 0); // Adjust to zero-based index, ensuring non-negative
        Pageable pageable = PageRequest.of(adjustedPage, size, sort);
        log.debug("Created PageRequest: page={}, size={}, sort={}", adjustedPage, size, sort);
        return pageable;
    }
}
