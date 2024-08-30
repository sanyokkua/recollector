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
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.exception.IllegalSpecificationParamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategorySpecificationTest {

    @Mock
    private Root<Category> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Predicate predicate;
    @InjectMocks
    private CategorySpecification categorySpecification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toPredicate_userIdIsNull_throwsIllegalSpecificationParamException() {
        categorySpecification = CategorySpecification.builder().userId(null).categoryName("Name").build();

        IllegalSpecificationParamException thrown = assertThrows(IllegalSpecificationParamException.class,
                                                                 () -> categorySpecification.toPredicate(root,
                                                                                                         query,
                                                                                                         cb));

        assertEquals("CategorySpecification requires userId", thrown.getMessage());
    }
}