package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
