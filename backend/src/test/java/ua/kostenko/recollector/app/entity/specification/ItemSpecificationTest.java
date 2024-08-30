package ua.kostenko.recollector.app.entity.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.exception.IllegalSpecificationParamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemSpecificationTest {

    @Mock
    private Root<Item> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Predicate predicate;
    @InjectMocks
    private ItemSpecification itemSpecification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toPredicate_userIdIsNull_throwsIllegalSpecificationParamException() {
        itemSpecification = ItemSpecification.builder().userId(null).categoryId(1L).build();

        IllegalSpecificationParamException thrown = assertThrows(IllegalSpecificationParamException.class,
                                                                 () -> itemSpecification.toPredicate(root, query, cb));

        assertEquals("ItemSpecification requires userId", thrown.getMessage());
    }

    @Test
    void toPredicate_categoryIdIsNull_throwsIllegalSpecificationParamException() {
        itemSpecification = ItemSpecification.builder().userId(1L).categoryId(null).build();

        IllegalSpecificationParamException thrown = assertThrows(IllegalSpecificationParamException.class,
                                                                 () -> itemSpecification.toPredicate(root, query, cb));

        assertEquals("ItemSpecification requires categoryId", thrown.getMessage());
    }
}