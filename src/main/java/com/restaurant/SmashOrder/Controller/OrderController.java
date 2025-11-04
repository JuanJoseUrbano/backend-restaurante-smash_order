package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.DTO.OrderDTO;
import com.restaurant.SmashOrder.Entity.Order;
import com.restaurant.SmashOrder.Entity.Product;
import com.restaurant.SmashOrder.IService.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping("/paginated")
    public ResponseEntity<Page<OrderDTO>> getCategoriesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Page<OrderDTO> orders = orderService.getOrdersPaginated(page, size);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/without-invoice")
    public List<OrderDTO> getOrdersWithoutInvoice() {
        return orderService.getOrdersWithoutInvoice();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<OrderDTO> order = orderService.getOrderById(id);
        return order.isPresent()
                ? ResponseEntity.ok(order.get())
                : ResponseEntity.status(404).body("Order not found with id: " + id);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderDTO> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
    @GetMapping("/search-by-customer")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerName(@RequestParam String name) {
        List<OrderDTO> orders = orderService.getOrdersByCustomerName(name);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public List<OrderDTO> getOrdersByStatus(@PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/date")
    public List<OrderDTO> getOrdersByDate(@RequestParam String date) {
        LocalDateTime parsedDate = LocalDateTime.parse(date);
        return orderService.getOrdersByDate(parsedDate);
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countAllOrders() {
        Long totalOrders = orderService.countAllOrders();
        return ResponseEntity.ok(totalOrders);
    }

    @GetMapping("/customer/{customerId}/count")
    public ResponseEntity<Long> countOrdersByCustomer(@PathVariable Long customerId) {
        Long totalOrdersByCustomer = orderService.countOrdersByCustomer(customerId);
        return ResponseEntity.ok(totalOrdersByCustomer);
    }
}

