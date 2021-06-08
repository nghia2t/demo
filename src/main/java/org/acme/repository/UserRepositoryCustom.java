package org.acme.repository;

import org.acme.entity.Users;

import java.util.List;
import java.util.Set;

public interface UserRepositoryCustom {
    List<Users> findUsers(String firstName, String lastName, String street, String houseNumber, String grade);
}
