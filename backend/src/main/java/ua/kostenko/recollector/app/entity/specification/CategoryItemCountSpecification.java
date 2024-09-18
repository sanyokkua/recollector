package ua.kostenko.recollector.app.entity.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ua.kostenko.recollector.app.entity.CategoryItemCount;
import ua.kostenko.recollector.app.exception.IllegalSpecificationParamException;

import java.util.Objects;

/**
 * Specification for querying {@link CategoryItemCount} entities based on various criteria.
 * <p>
 * Implements the {@link Specification} interface to provide dynamic query construction for {@link CategoryItemCount} entities.
 * The criteria include user ID and optional category name.
 * </p>
 *
 * <p>The class uses JPA Criteria API to build the query predicate dynamically based on the provided parameters.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemCountSpecification implements Specification<CategoryItemCount> {

    /**
     * User ID to filter category item counts.
     * <p>
     * This field is mandatory for the specification to be valid.
     * </p>
     */
    private Long userId;

    /**
     * Category name to filter category item counts by partial match.
     * <p>
     * If provided, the query will include counts where the category name contains this value (case-insensitive).
     * </p>
     */
    private String categoryName;

    /**
     * Constructs the query predicate based on the provided criteria.
     * <p>
     * The method builds a {@link Predicate} object to filter {@link CategoryItemCount} entities according to the specified criteria.
     * </p>
     *
     * @param root  The root of the query from which attributes are fetched.
     * @param query The query object being constructed.
     * @param cb    The CriteriaBuilder used to construct the predicate.
     *
     * @return The constructed {@link Predicate} object based on the provided criteria.
     *
     * @throws IllegalSpecificationParamException If the required parameter (userId) is missing.
     */
    @Override
    public Predicate toPredicate(Root<CategoryItemCount> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (Objects.isNull(userId)) {
            throw new IllegalSpecificationParamException("CategoryItemCountSpecification requires userId");
        }

        // Initialize the predicate with a conjunction (true predicate)
        Predicate predicate = cb.conjunction();

        // Filter by userId
        predicate = cb.and(predicate, cb.equal(root.get("userId"), userId));

        // Filter by categoryName if it is not blank
        if (StringUtils.isNotBlank(categoryName)) {
            predicate = cb.and(predicate,
                               cb.like(cb.lower(root.get("categoryName")), "%" + categoryName.toLowerCase() + "%"));
        }

        return predicate;
    }
}
