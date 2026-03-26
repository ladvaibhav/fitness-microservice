package com.fitness.activityservice.controller;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request){
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ActivityResponse>> getAllActivities(){
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/userId")
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("X-User-Id") String userId){
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String id) {
        return ResponseEntity.ok(activityService.getActivity(id));
    }


}
