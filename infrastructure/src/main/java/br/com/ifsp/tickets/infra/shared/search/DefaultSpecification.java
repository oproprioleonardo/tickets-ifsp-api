package br.com.ifsp.tickets.infra.shared.search;

import br.com.ifsp.tickets.domain.shared.search.SearchFilter;
import br.com.ifsp.tickets.domain.shared.search.SearchOperation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class DefaultSpecification<T> implements Specification<T> {

    private final SearchFilter searchFilter;

    public DefaultSpecification(SearchFilter searchFilter) {
        this.searchFilter = searchFilter;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<T> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
        final SearchOperation operation = SearchOperation.getSimpleOperation(searchFilter.operation());

        if (operation != null) {
            return switch (operation) {
                case EQUAL -> criteriaBuilder.equal(root.get(searchFilter.filterKey()), searchFilter.value());
                case NOT_EQUAL -> criteriaBuilder.notEqual(root.get(searchFilter.filterKey()), searchFilter.value());
                case GREATER_THAN ->
                        criteriaBuilder.greaterThan(root.get(searchFilter.filterKey()), searchFilter.value().toString());
                case LESS_THAN ->
                        criteriaBuilder.lessThan(root.get(searchFilter.filterKey()), searchFilter.value().toString());
                case CONTAINS ->
                        criteriaBuilder.like(root.get(searchFilter.filterKey()), "%" + searchFilter.value() + "%");
                case INSENSITIVE_CONTAINS ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(searchFilter.filterKey())), "%" + searchFilter.value().toString().toLowerCase() + "%");
                case BEGINS_WITH ->
                        criteriaBuilder.like(root.get(searchFilter.filterKey()), searchFilter.value() + "%");
                case INSENSITIVE_BEGINS_WITH ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(searchFilter.filterKey())), searchFilter.value().toString().toLowerCase() + "%");
                case ENDS_WITH -> criteriaBuilder.like(root.get(searchFilter.filterKey()), "%" + searchFilter.value());
                case INSENSITIVE_ENDS_WITH ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(searchFilter.filterKey())), "%" + searchFilter.value().toString().toLowerCase());
                case DOES_NOT_CONTAIN ->
                        criteriaBuilder.notLike(root.get(searchFilter.filterKey()), "%" + searchFilter.value() + "%");
                case DOES_NOT_BEGIN_WITH ->
                        criteriaBuilder.notLike(root.get(searchFilter.filterKey()), searchFilter.value() + "%");
                case DOES_NOT_END_WITH ->
                        criteriaBuilder.notLike(root.get(searchFilter.filterKey()), "%" + searchFilter.value());
                case GREATER_THAN_EQUAL ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get(searchFilter.filterKey()), searchFilter.value().toString());
                case LESS_THAN_EQUAL ->
                        criteriaBuilder.lessThanOrEqualTo(root.get(searchFilter.filterKey()), searchFilter.value().toString());
                case NUL -> criteriaBuilder.isNull(root.get(searchFilter.filterKey()));
                case NOT_NULL -> criteriaBuilder.isNotNull(root.get(searchFilter.filterKey()));
            };
        }

        return null;
    }
}
