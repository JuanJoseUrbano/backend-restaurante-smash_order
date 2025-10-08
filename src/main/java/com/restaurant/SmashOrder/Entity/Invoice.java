package com.restaurant.SmashOrder.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @NotNull(message = "El pedido es obligatorio")
    private Order order;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "receipt_number", unique = true, length = 50)
    @Size(max = 50, message = "El número de recibo no puede exceder 50 caracteres")
    private String receiptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "El estado del pago es obligatorio")
    private PaymentStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
