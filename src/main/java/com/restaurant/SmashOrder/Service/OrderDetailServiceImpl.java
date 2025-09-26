package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Order;
import com.restaurant.SmashOrder.Entity.OrderDetail;
import com.restaurant.SmashOrder.Entity.Product;
import com.restaurant.SmashOrder.Repository.OrderDetailRepository;
import com.restaurant.SmashOrder.Repository.OrderRepository;
import com.restaurant.SmashOrder.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    public Optional<OrderDetail> getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    public ResponseEntity<String> addOrderDetail(Long orderId, List<OrderDetail> details) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Orden no encontrada");
        }

        Order order = orderOpt.get();

        // Guardamos el total antes de agregar los nuevos detalles
        BigDecimal totalBefore = order.getOrderDetails().stream()
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAdded = BigDecimal.ZERO;

        for (OrderDetail detail : details) {
            Optional<Product> productOpt = productRepository.findById(detail.getProduct().getId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + detail.getProduct().getId());
            }

            Product product = productOpt.get();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));

            detail.setOrder(order);
            detail.setProduct(product);
            detail.setSubtotal(subtotal);

            // Guardar el detalle
            orderDetailRepository.save(detail);

            // Agregar el detalle a la orden
            order.getOrderDetails().add(detail);

            totalAdded = totalAdded.add(subtotal);
        }

        // Recalcular el total de la orden
        BigDecimal totalAfter = order.getOrderDetails().stream()
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(totalAfter);

        // Guardar la orden actualizada
        orderRepository.save(order);

        return ResponseEntity.ok("Total antes de agregar detalles: $" + totalBefore
                + ". Total agregado: $" + totalAdded
                + ". Nuevo total de la orden: $" + totalAfter);
    }




    @Override
    public ResponseEntity<String> updateOrderDetail(Long id, OrderDetail updatedDetail) {
        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findById(id);
        if (existingDetailOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderDetail existingDetail = existingDetailOpt.get();

        Optional<Product> productOpt = productRepository.findById(updatedDetail.getProduct().getId());
        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Producto no encontrado");
        }

        Product product = productOpt.get();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(updatedDetail.getQuantity()));

        existingDetail.setProduct(product);
        existingDetail.setQuantity(updatedDetail.getQuantity());
        existingDetail.setSubtotal(subtotal);

        orderDetailRepository.save(existingDetail);

        return ResponseEntity.ok("Detalle actualizado con subtotal: $" + subtotal);
    }

    @Override
    public ResponseEntity<String> deleteOrderDetail(Long detailId) {
        Optional<OrderDetail> detailOpt = orderDetailRepository.findById(detailId);
        if (detailOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Detalle no encontrado");
        }

        OrderDetail detail = detailOpt.get();
        Order order = detail.getOrder();

        // Eliminar el detalle
        orderDetailRepository.delete(detail);

        // Recalcular el total de la orden
        BigDecimal total = order.getOrderDetails().stream()
                .filter(d -> !d.getId().equals(detailId)) // ignorar el detalle eliminado
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

        // Guardar la orden actualizada
        orderRepository.save(order);

        return ResponseEntity.ok("Detalle eliminado. Total actualizado: $" + total);
    }

}