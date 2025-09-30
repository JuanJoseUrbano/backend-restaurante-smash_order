package com.restaurant.SmashOrder.DTO;

import com.restaurant.SmashOrder.Entity.TableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private UserDTO customer;
    private TableEntity table;
    private LocalDateTime date;
    private Boolean status;
}
