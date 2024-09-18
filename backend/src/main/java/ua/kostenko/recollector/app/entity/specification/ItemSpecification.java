package ua.kostenko.recollector.app.entity.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.IllegalSpecificationParamException;

import java.util.Objects;

/**
 * Specification for querying {@link Item} entities based on various criteria.
 * <p>
 * Implements the {@link Specification} interface to provide dynamic query construction for {@link Item} entities.
 * The criteria include user ID, category ID, item name, and item status.
 * </p>
 *
 * <p>The class uses JPA Criteria API to build the query predicate dynamically based on the provided parameters.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSpecification implements Specification<Item> {

    /**
     * User ID to filter items.
     * <p>
     * This field is mandatory for the specification to be valid.
     * </p>
     */
    private Long userId;

    /**
     * Category ID to filter items.
     * <p>
     * This field is mandatory for the specification to be valid.
     * </p>
     */
    private Long categoryId;

    /**
     * Item name to filter items by partial match.
     * <p>
     * If provided, the query will include items whose names contain this value (case-insensitive).
     * </p>
     */
    private String itemName;

    /**
     * Item status to filter items by exact match.
     * <p>
     * If provided, the query will include items with this exact status.
     * </p>
     */
    private String itemStatus;

    /**
     * Constructs the query predicate based on the provided criteria.
     * <p>
     * The method builds a {@link Predicate} object to filter {@link Item} entities according to the specified criteria.
     * </p>
     *
     * @param root  The root of the query from which attributes are fetched.
     * @param query The query object being constructed.
     * @param cb    The CriteriaBuilder used to construct the predicate.
     *
     * @return The constructed {@link Predicate} object based on the provided criteria.
     *
     * @throws IllegalSpecificationParamException If required parameters (userId or categoryId) are missing.
     */
    @Override
    public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (Objects.isNull(userId)) {
            throw new IllegalSpecificationParamException("ItemSpecification requires userId");
        }
        if (Objects.isNull(categoryId)) {
            throw new IllegalSpecificationParamException("ItemSpecification requires categoryId");
        }

        Predicate predicate = cb.conjunction();

        // Join with Category and User entities to apply user and category filters
        Join<Item, Category> categoryJoin = root.join("category", JoinType.INNER);
        Join<Category, User> userJoin = categoryJoin.join("user", JoinType.INNER);

        // Filter by userId and categoryId
        predicate = cb.and(predicate, cb.equal(userJoin.get("userId"), userId));
        predicate = cb.and(predicate, cb.equal(categoryJoin.get("categoryId"), categoryId));

        // Filter by itemName if provided
        if (StringUtils.isNotBlank(itemName)) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("itemName")), "%" + itemName.toLowerCase() + "%"));
        }

        // Filter by itemStatus if provided
        if (StringUtils.isNotBlank(itemStatus)) {
            predicate = cb.and(predicate, cb.equal(root.get("itemStatus"), itemStatus));
        }

        return predicate;
    }
}
