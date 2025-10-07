package com.restaurant.SmashOrder.DTO;

import com.restaurant.SmashOrder.Entity.TableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private UserDTO customer;
    private TableEntity table;
    private BigDecimal total;
    private LocalDateTime date;
    private String status;
}
