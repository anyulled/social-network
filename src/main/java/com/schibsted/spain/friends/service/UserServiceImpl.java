package com.schibsted.spain.friends.service;

import com.google.common.base.Preconditions;
import com.schibsted.spain.friends.dto.UserDTO;
import com.schibsted.spain.friends.entity.User;
import com.schibsted.spain.friends.repository.UserRepository;
import com.schibsted.spain.friends.utils.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends AbstractUserDetailsAuthenticationProvider implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * User signup
     *
     * @param username username
     * @param password password
     * @return returns the signed up user
     */
    @Override
    public UserDTO signup(String username, String password) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Signup attempt: user {}", username);
            }
            Preconditions.checkNotNull(username, "Username cannot be null");
            Preconditions.checkNotNull(password, "Password cannot be null");
            Preconditions.checkArgument(username.matches("[a-zA-Z0-9]{5,10}+"), String.format("Invalid format: username  %s should be alphanumeric and between 5 and 10 characters", username));
            Preconditions.checkArgument(password.matches("[a-zA-Z0-9]{8,12}+"), String.format("Invalid format: password %s should be alphanumeric and between 8 and 12 characters", password));
            User result = userRepository.addUser(username, password);
            return result.toDto();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Signup error: {}", e.getMessage());
            throw new ValidationException(ErrorDto.builder()
                    .message(e.getMessage())
                    .exceptionClass(this.getClass().getSimpleName())
                    .build());
        }
    }

    /**
     * Add Users as friends to each other
     *
     * @param username  - user
     * @param username2 - user
     * @return returns the user updated with the friend added
     */
    @Override
    public UserDTO addFriend(String username, String username2) {
        User userFriend = userRepository.getUser(username);
        User userFriend2 = userRepository.getUser(username2);

        userFriend = userRepository.addFriend(userFriend, userFriend2);
        return userFriend.toDto();
    }

    /**
     * Looks for a user by its username an password
     *
     * @param username username
     * @param password password
     * @return returns the user or an {@link UnauthorizedException} otherwise
     */
    @Override
    public UserDTO authenticate(String username, String password) {
        User user;
        try {
            user = userRepository.findUser(username, password);
        } catch (InvalidCredentialException e) {
            log.error("user {} not found", username);
            throw new UnauthorizedException(ErrorDto.builder()
                    .message(e.getMessage())
                    .exceptionClass(this.getClass().getSimpleName()).build());
        }
        return user.toDto();
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        // do nothing
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting authentication for user {} with credentials {}", username, authentication.getCredentials().toString());
        }
        return userRepository.findUser(username, authentication.getCredentials().toString());
    }
}
