package com.fitness.activityservice.listener;

import com.fitness.activityservice.entity.Activity;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.time.LocalDateTime;

@Configuration
public class ActivityListener extends AbstractMongoEventListener<Activity> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Activity> event) {
        Activity activity = event.getSource();
        LocalDateTime now = LocalDateTime.now();

        if (activity.getCreatedAt()==null) {
            activity.setCreatedAt(now);
        }
        activity.setUpdatedAt(now);
    }
}
