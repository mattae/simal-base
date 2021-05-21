package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.Notification;
import com.mattae.simal.modules.base.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationResource {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public List<Notification> getNotifications() {
        return notificationService.getNotifications();
    }

    @PostMapping("/notifications/mark-as-read")
    public void markAsRead(@RequestBody List<Notification> notifications) {
        notificationService.markAsRead(notifications);
    }
}
