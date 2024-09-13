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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemCountSpecification implements Specification<CategoryItemCount> {

    private Long userId;
    private String categoryName;

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
            predicate = cb.and(predicate, cb.like(root.get("categoryName"), "%" + categoryName + "%"));
        }

        return predicate;
    }
}
