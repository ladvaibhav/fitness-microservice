package com.fitness.userservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "firstName",
        "lastName",
        "email",
        "createdAt",
        "updatedAt"
})

@Data
public class UserResponse {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
