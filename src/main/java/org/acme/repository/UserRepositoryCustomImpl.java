package org.acme.repository;

import org.acme.entity.Users;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Users> findUsers(String firstName, String lastName, String street, String houseNumber, String grade) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Users> query = cb.createQuery(Users.class);
        Root<Users> user = query.from(Users.class);

        List<Predicate> predicates = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("street", street);
        params.put("houseNumber", houseNumber);
        params.put("grade", grade);
        params.entrySet().stream()
                .filter(x -> StringUtils.hasText(x.getValue()))
                .map(x -> cb.equal(user.get(x.getKey()), x.getValue()))
                .forEach( x -> predicates.add(x));

        query.select(user)
                .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        return entityManager.createQuery(query)
                .getResultList();
    }
}
