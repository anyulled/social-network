package com.schibsted.spain.friends.repository;

import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import com.schibsted.spain.friends.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private Set<User> users = new HashSet<>();

    public Boolean addUser(String username, String password) {
        return users.add(User.builder().username(username).password(password).build());
    }

    @Override
    public User getUser(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public User findUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(InvalidCredentialException::new);
    }

    @Override
    public Boolean updateUser(User user) {
        return users.remove(user) && users.add(user);
    }
}
