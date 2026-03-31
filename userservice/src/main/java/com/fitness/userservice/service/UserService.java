package com.fitness.userservice.service;

import com.fitness.userservice.dto.UserCreateResponse;
import com.fitness.userservice.dto.UserRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.entity.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private UserRepository repository;

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;

    }


    public UserCreateResponse createUserProfile(@Valid UserRequest request) {

        if (repository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = repository.save(user);

        UserCreateResponse userCreateResponse = new UserCreateResponse();
        userCreateResponse.setId(savedUser.getId());
        userCreateResponse.setEmail(savedUser.getEmail());
        userCreateResponse.setPassword(savedUser.getPassword());
        userCreateResponse.setFirstName(savedUser.getFirstName());
        userCreateResponse.setLastName(savedUser.getLastName());
        userCreateResponse.setCreatedAt(savedUser.getCreatedAt());
        userCreateResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userCreateResponse;
    }

    public List<UserResponse> getAllUser() {
        return repository.findAll()
                .stream()
                .map(user -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setFirstName(user.getFirstName());
                    userResponse.setLastName(user.getLastName());
                    userResponse.setCreatedAt(user.getCreatedAt());
                    userResponse.setUpdatedAt(user.getUpdatedAt());
                    return userResponse;
                })
                .toList();

    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for UserID: "+userId);
        return repository.existsById(userId);
    }
}
