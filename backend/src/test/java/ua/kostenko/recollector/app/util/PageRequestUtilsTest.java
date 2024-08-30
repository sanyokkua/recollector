package ua.kostenko.recollector.app.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import ua.kostenko.recollector.app.dto.response.MetaInfo;
import ua.kostenko.recollector.app.dto.response.PaginationInfo;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestUtilsTest {

    @Test
    void buildMetaInfo_whenPageHasSort_returnsCorrectMetaInfo() {
        Page<String> page = new PageImpl<>(Collections.singletonList("item"),
                                           PageRequest.of(0, 10, Sort.by("name").descending()),
                                           100);

        MetaInfo metaInfo = PageRequestUtils.buildMetaInfo(page);
        assertNotNull(metaInfo, "MetaInfo should not be null");

        PaginationInfo pagination = metaInfo.getPagination();
        assertNotNull(pagination, "PaginationInfo should not be null");
        assertEquals(1, pagination.getCurrentPage(), "Current page should be 1");
        assertEquals(1, pagination.getItemsPerPage(), "Items per page should be 1");
        assertEquals(10, pagination.getTotalPages(), "Total pages should be 10");
        assertEquals(100, pagination.getTotalItems(), "Total items should be 100");
        assertEquals("name", pagination.getSortField(), "Sort field should be 'name'");
        assertEquals("DESC", pagination.getSortDirection(), "Sort direction should be 'DESC'");
    }

    @Test
    void buildMetaInfo_whenPageHasNoSort_returnsMetaInfoWithEmptySort() {
        Page<String> page = new PageImpl<>(Collections.singletonList("item"), PageRequest.of(0, 10), 100);

        MetaInfo metaInfo = PageRequestUtils.buildMetaInfo(page);
        assertNotNull(metaInfo, "MetaInfo should not be null");

        PaginationInfo pagination = metaInfo.getPagination();
        assertNotNull(pagination, "PaginationInfo should not be null");
        assertEquals(1, pagination.getCurrentPage(), "Current page should be 1");
        assertEquals(1, pagination.getItemsPerPage(), "Items per page should be 1");
        assertEquals(10, pagination.getTotalPages(), "Total pages should be 10");
        assertEquals(100, pagination.getTotalItems(), "Total items should be 100");
        assertEquals("", pagination.getSortField(), "Sort field should be empty");
        assertEquals("", pagination.getSortDirection(), "Sort direction should be empty");
    }

    @Test
    void buildMetaInfo_whenPageIsEmpty_returnsMetaInfoWithZeroValues() {
        Page<String> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        MetaInfo metaInfo = PageRequestUtils.buildMetaInfo(page);
        assertNotNull(metaInfo, "MetaInfo should not be null");

        PaginationInfo pagination = metaInfo.getPagination();
        assertNotNull(pagination, "PaginationInfo should not be null");
        assertEquals(1, pagination.getCurrentPage(), "Current page should be 1");
        assertEquals(0, pagination.getItemsPerPage(), "Items per page should be 0");
        assertEquals(0, pagination.getTotalPages(), "Total pages should be 0");
        assertEquals(0, pagination.getTotalItems(), "Total items should be 0");
        assertEquals("", pagination.getSortField(), "Sort field should be empty");
        assertEquals("", pagination.getSortDirection(), "Sort direction should be empty");
    }

    @Test
    void buildMetaInfo_whenPageIsNull_returnsMetaInfoWithNullPagination() {
        MetaInfo actual = PageRequestUtils.buildMetaInfo(null);

        assertNotNull(actual, "MetaInfo should not be null for a null Page");
        assertNull(actual.getPagination(), "Pagination should be null for a null Page");
    }

    @Test
    void createPageRequest_whenValidInput_returnsPageable() {
        Pageable pageable = PageRequestUtils.createPageRequest(2, 10, Sort.by("name").ascending());

        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(1, pageable.getPageNumber(), "Page number should be 1 (zero-based index)");
        assertEquals(10, pageable.getPageSize(), "Page size should be 10");
        assertEquals(Sort.by("name").ascending(), pageable.getSort(), "Sort should be by 'name' ascending");
    }

    @Test
    void createPageRequest_whenPageNumberIsLessThanOne_returnsPageableWithZeroBasedIndex() {
        Pageable pageable = PageRequestUtils.createPageRequest(0, 10, Sort.by("name").ascending());

        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(0, pageable.getPageNumber(), "Page number should be 0 (zero-based index)");
        assertEquals(10, pageable.getPageSize(), "Page size should be 10");
        assertEquals(Sort.by("name").ascending(), pageable.getSort(), "Sort should be by 'name' ascending");
    }

    @Test
    void createPageRequest_whenPageNumberIsZero_returnsPageableWithZeroBasedIndex() {
        Pageable pageable = PageRequestUtils.createPageRequest(1, 10, Sort.by("name").ascending());

        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(0, pageable.getPageNumber(), "Page number should be 0 (zero-based index)");
        assertEquals(10, pageable.getPageSize(), "Page size should be 10");
        assertEquals(Sort.by("name").ascending(), pageable.getSort(), "Sort should be by 'name' ascending");
    }

    @Test
    void createPageRequest_whenNoSort_returnsPageableWithUnsorted() {
        Pageable pageable = PageRequestUtils.createPageRequest(2, 10, Sort.unsorted());

        assertNotNull(pageable, "Pageable should not be null");
        assertEquals(1, pageable.getPageNumber(), "Page number should be 1 (zero-based index)");
        assertEquals(10, pageable.getPageSize(), "Page size should be 10");
        assertEquals(Sort.unsorted(), pageable.getSort(), "Sort should be unsorted");
    }

    @Test
    void createPageRequest_whenInvalidSize_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                     () -> PageRequestUtils.createPageRequest(2, -1, Sort.by("name").ascending()));
    }
}
