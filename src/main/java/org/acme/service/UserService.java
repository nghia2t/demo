package org.acme.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.util.StringUtil;
import org.acme.entity.Users;
import org.acme.model.Address;
import org.acme.model.User;
import org.acme.repository.UsersRepository;
import org.springframework.util.ObjectUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UsersRepository usersRepository;

    @Inject
    ObjectMapper objectMapper;

    public User getUser(Long id) {
        Users u = usersRepository.findById(id).orElse(null);
        if(u == null){
            throw new WebApplicationException(404);
        }
        return toModel(u);
    }

    public List<User> searchUsers(String firstName, String lastName, String street, String houseNumber, String grade) {
        return usersRepository.findUsers(firstName, lastName, street, houseNumber, grade).stream()
                .map(x -> toModel(x))
                .collect(Collectors.toList());
    }

    @Transactional
    public User createUser(User user) {
        if (!ObjectUtils.isEmpty(user.getId()) && usersRepository.existsById(user.getId())){
            throw new WebApplicationException(409);
        }
        Users u = toEntity(user);
        return toModel(usersRepository.save(u));
    }

    @Transactional
    public User updateUser(Long id, User user) {
        if(ObjectUtils.isEmpty(id) || !id.equals(user.getId())){
            throw new WebApplicationException(400);
        }
        if (!usersRepository.existsById(id)){
            throw new WebApplicationException(404);
        }
        Users u = toEntity(user);
        return toModel(usersRepository.save(u));
    }

    @Transactional
    public void deleteUser(Long id) {
        if(!usersRepository.existsById(id)){
            throw new WebApplicationException(404);
        }
        usersRepository.deleteById(id);
    }



    User toModel(Users u){
        User user = objectMapper.convertValue(u, User.class);
        Address address = new Address();
        address.setHouseNumber(u.getHouseNumber());
        address.setStreet(u.getStreet());
        address.setCountry(u.getCountry());
        user.setAddress(address);
        return user;
    }

    Users toEntity(User u){
        Users user = objectMapper.convertValue(u, Users.class);
        if(!ObjectUtils.isEmpty(u.getAddress())){
            user.setHouseNumber(u.getAddress().getHouseNumber());
            user.setStreet(u.getAddress().getStreet());
            user.setCountry(u.getAddress().getCountry());
        }
        return user;
    }


}