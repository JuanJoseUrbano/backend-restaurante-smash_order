package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOrderCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Notification> findByOrderCustomerIdAndReadIsFalseOrderByCreatedAtDesc(Long userId);


}
