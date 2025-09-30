package com.restaurant.SmashOrder.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
    public class Reservation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "customer_id", nullable = false)
        private User customer;

        @ManyToOne
        @JoinColumn(name = "table_id", nullable = false)
        private TableEntity table;

        @Column(name = "reservation_date", nullable = false)
        private LocalDateTime date;

        @Column(name = "status", nullable = false)
        private Boolean status;

}
