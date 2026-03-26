package com.fitness.userservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "firstName",
        "lastName",
        "email",
        "password",
        "createdAt",
        "updatedAt"
})
@Data
public class UserCreateResponse {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
