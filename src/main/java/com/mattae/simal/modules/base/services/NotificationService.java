package com.mattae.simal.modules.base.services;

import com.mattae.simal.modules.base.domain.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import com.mattae.simal.modules.base.domain.entities.Notification;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @PostFilter("filterObject.user != null ? filterObject.user.username == principal.username : true ")
    public List<Notification> getNotifications() {
        return notificationRepository.findByReadIsFalse();
    }

    @PreFilter("filterObject.user == null or filterObject.user.username == principal.username")
    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}
