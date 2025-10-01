package com.restaurant.SmashOrder.DTO;

import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Long id;
    private OrderSummaryDTO order;
    private LocalDateTime paymentDate;
    private String receiptNumber;
    private PaymentStatus status;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
