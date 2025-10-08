package com.restaurant.SmashOrder.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private OrderSummaryDTO order;
    private String type;
    private String message;
}
