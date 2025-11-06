package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.NotificationDTO;
import com.restaurant.SmashOrder.Entity.Notification;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<NotificationDTO> getAllNotifications();
    Optional<NotificationDTO> getNotificationById(Long id);
    List<NotificationDTO> getNotificationsByCustomer(Long customerId);
    List<NotificationDTO> getNotificationsUnreadByCustomer(Long customerId);
    ResponseEntity<String> createNotification(Notification notification);
    ResponseEntity<String> markNotificationAsRead(Long id);
    ResponseEntity<String> updateNotification(Long id, Notification notification);
    ResponseEntity<String> deleteNotification(Long id);
}
