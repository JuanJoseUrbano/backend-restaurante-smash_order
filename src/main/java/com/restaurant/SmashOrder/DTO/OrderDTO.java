package com.restaurant.SmashOrder.DTO;

import com.restaurant.SmashOrder.Entity.OrderDetail;
import com.restaurant.SmashOrder.Entity.TableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private UserDTO customer;
    private TableEntity table;
    private List<OrderDetail> orderDetails;
    private BigDecimal total;
    private LocalDateTime date;
    private String status;
}
