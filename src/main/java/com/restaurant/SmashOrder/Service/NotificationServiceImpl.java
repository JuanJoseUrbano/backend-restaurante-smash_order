package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.DTO.NotificationDTO;
import com.restaurant.SmashOrder.DTO.OrderSummaryDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.Notification;
import com.restaurant.SmashOrder.Entity.Order;
import com.restaurant.SmashOrder.IService.NotificationService;
import com.restaurant.SmashOrder.Repository.NotificationRepository;
import com.restaurant.SmashOrder.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    @Override
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToNotificationDTO)
                .toList();
    }

    @Override
    public Optional<NotificationDTO> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::mapToNotificationDTO);
    }

    @Override
    public List<NotificationDTO> getNotificationsByCustomer(Long customerId) {
        return notificationRepository.findByOrderCustomerId(customerId)
                .stream()
                .map(this::mapToNotificationDTO)
                .toList();
    }


    @Override
    public ResponseEntity<String> createNotification(Notification notification) {
        try {
            if (notification.getOrder() == null ||
                    !orderRepository.existsById(notification.getOrder().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid order: Order not found.");
            }

            Order order = orderRepository.findById(notification.getOrder().getId()).orElseThrow();
            notification.setOrder(order);
            notificationRepository.save(notification);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Notification created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating notification: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateNotification(Long id, Notification updatedNotification) {
        try {
            Notification existingNotification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));

            if (updatedNotification.getOrder() == null ||
                    !orderRepository.existsById(updatedNotification.getOrder().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid order: Order not found.");
            }

            Order order = orderRepository.findById(updatedNotification.getOrder().getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            existingNotification.setOrder(order);
            existingNotification.setType(updatedNotification.getType() != null
                    ? updatedNotification.getType()
                    : existingNotification.getType());
            existingNotification.setMessage(updatedNotification.getMessage() != null
                    ? updatedNotification.getMessage()
                    : existingNotification.getMessage());
            notificationRepository.save(existingNotification);

            return ResponseEntity.ok("Notification updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating notification: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteNotification(Long id) {
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(id);

            if (notificationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró la notificación con ID: " + id);
            }

            notificationRepository.deleteById(id);
            return ResponseEntity.ok("Notificación eliminada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la notificación: " + e.getMessage());
        }
    }

    private NotificationDTO mapToNotificationDTO(Notification notification) {
        if (notification == null) return null;

        return new NotificationDTO(
                notification.getId(),
                mapToOrderSummaryDTO(notification.getOrder()),
                notification.getType(),
                notification.getMessage()
        );
    }
    private OrderSummaryDTO mapToOrderSummaryDTO(Order order) {
        if (order == null) return null;

        return new OrderSummaryDTO(order.getId(),new UserDTO(
                order.getCustomer().getId(),
                order.getCustomer().getUserName(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getCustomer().getRoles()),
                order.getTable(),
                order.getTotal(),
                order.getDate(),
                order.getStatus()
        );
    }
}
