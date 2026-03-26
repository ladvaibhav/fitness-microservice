package com.fitness.activityservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fitness.activityservice.enums.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@JsonPropertyOrder({
        "id",
        "userId",
        "type",
        "durationMinutes",
        "caloriesBurned",
        "startTime",
        "additionalMetrics",
        "createdAt",
        "updatedAt"
})

@Data
public class ActivityResponse {
    private String id;
    private String userId;
    private ActivityType type;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
