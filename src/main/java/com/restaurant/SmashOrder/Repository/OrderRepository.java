package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.invoice IS NULL")
    List<Order> findOrdersWithoutInvoice();
    @Query("SELECT COUNT(o) FROM Order o")
    Long countAllOrders();
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Long countOrdersByCustomer(@Param("customerId") Long customerId);

    List<Order> findByCustomerId(Long customerId);
    List<Order> findByStatus(String status);
    List<Order> findByDate(LocalDateTime date);
    List<Order> findAllByOrderByDateDesc();
}
