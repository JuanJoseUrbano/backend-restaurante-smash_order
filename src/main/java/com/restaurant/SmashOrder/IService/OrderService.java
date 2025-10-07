    package com.restaurant.SmashOrder.IService;

    import com.restaurant.SmashOrder.DTO.OrderDTO;
    import com.restaurant.SmashOrder.Entity.Order;
    import org.springframework.data.repository.query.Param;
    import org.springframework.http.ResponseEntity;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

    public interface OrderService {
        List<OrderDTO> getAllOrders();
        List<OrderDTO> getOrdersWithoutInvoice();
        Optional<OrderDTO> getOrderById(Long id);
        List<OrderDTO> getOrdersByCustomer(Long customerId);

        List<OrderDTO> getOrdersByStatus(String status);

        List<OrderDTO> getOrdersByDate(LocalDateTime date);

        ResponseEntity<String> createOrder(Order order);

        ResponseEntity<String> updateOrder(Long id, Order order);

        ResponseEntity<String> deleteOrder(Long id);
        Long countAllOrders();
        Long countOrdersByCustomer(Long customerId);
    }
