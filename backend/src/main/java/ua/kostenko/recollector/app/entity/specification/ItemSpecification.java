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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSpecification implements Specification<Item> {

    private Long userId;
    private Long categoryId;
    private String itemName;
    private String itemStatus;

    @Override
    public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (Objects.isNull(userId)) {
            throw new IllegalSpecificationParamException("ItemSpecification requires userId");
        }
        if (Objects.isNull(categoryId)) {
            throw new IllegalSpecificationParamException("ItemSpecification requires categoryId");
        }

        Predicate predicate = cb.conjunction();

        Join<Item, Category> categoryJoin = root.join("category", JoinType.INNER);
        Join<Category, User> userJoin = categoryJoin.join("user", JoinType.INNER);

        predicate = cb.and(predicate, cb.equal(userJoin.get("userId"), userId));
        predicate = cb.and(predicate, cb.equal(categoryJoin.get("categoryId"), categoryId));

        if (StringUtils.isNotBlank(itemName)) {
            predicate = cb.and(predicate, cb.like(root.get("itemName"), "%" + itemName + "%"));
        }

        if (StringUtils.isNotBlank(itemStatus)) {
            predicate = cb.and(predicate, cb.equal(root.get("itemStatus"), itemStatus));
        }

        return predicate;
    }
}
