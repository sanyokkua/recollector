package ua.kostenko.recollector.app.entity.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.entity.User;
import ua.kostenko.recollector.app.exception.IllegalSpecificationParamException;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class CategorySpecification implements Specification<Category> {

    private Long userId;
    private String categoryName;

    @Override
    public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (Objects.isNull(userId)) {
            throw new IllegalSpecificationParamException("CategorySpecification requires userId");
        }

        Predicate predicate = cb.conjunction();

        Join<Category, User> userJoin = root.join("user");
        predicate = cb.and(predicate, cb.equal(userJoin.get("userId"), userId));

        if (StringUtils.isNotBlank(categoryName)) {
            predicate = cb.and(predicate, cb.like(root.get("categoryName"), "%" + categoryName + "%"));
        }

        return predicate;
    }
}
