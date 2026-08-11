package com.prashant.bank.user.service;

import com.prashant.bank.user.dto.UserRequestDto;
import com.prashant.bank.user.dto.UserResponseDto;
import com.prashant.bank.user.entity.User;
import com.prashant.bank.user.exception.UserNotFoundException;
import com.prashant.bank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto saveUser(UserRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "User already exists with email: "
                            + request.getEmail()
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // NEVER store plain-text passwords
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setActive(true);

        User savedUser = userRepository.save(user);

        log.info(
                "User created successfully with id: {}",
                savedUser.getId()
        );

        return mapToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id)
                );

        return mapToResponse(user);
    }

    @Transactional
    public UserResponseDto updateUser(
            Long id,
            UserRequestDto request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id)
                );

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Update password only when supplied
        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        User updatedUser =
                userRepository.save(user);

        log.info(
                "User updated successfully with id: {}",
                id
        );

        return mapToResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id)
                );

        userRepository.delete(user);

        log.info(
                "User deleted successfully with id: {}",
                id
        );
    }

    private UserResponseDto mapToResponse(User user) {

        UserResponseDto response =
                new UserResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }
}