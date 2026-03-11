package org.barrikeit.service.filter.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.barrikeit.model.domain.Role;
import org.barrikeit.model.domain.User;
import org.barrikeit.util.constants.EntityConstants;
import org.springframework.data.jpa.domain.Specification;

@Builder(toBuilder = true)
@AllArgsConstructor
public class RoleFilterSpecification implements Specification<User> {

  private final String role;

  @Override
  public Predicate toPredicate(
      Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (role != null) {
      Join<User, Role> roleJoin = root.join(EntityConstants.ROLES);
      return criteriaBuilder.and(criteriaBuilder.equal(roleJoin.get(EntityConstants.CODE), role));
    }
    return null;
  }
}
