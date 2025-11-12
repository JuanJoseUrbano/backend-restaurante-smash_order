package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.ReservationDTO;
import com.restaurant.SmashOrder.Entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<ReservationDTO> getAllReservations();
    List<ReservationDTO> getOrdersByCustomer(Long customerId);
    Page<ReservationDTO> getReservationsPaginated(int page, int size);
    Page<ReservationDTO> getReservationsByStatusPaginated(boolean status, int page, int size);
    Optional<ReservationDTO> getReservationById(Long id);
    Page<ReservationDTO> getReservationsByDatePaginated(LocalDateTime date, int page, int size);
    ResponseEntity<String> createReservation(Reservation reservation);
    ResponseEntity<String> updateReservation(Long id, Reservation reservation);
    ResponseEntity<String> deleteReservation(Long id);
    Long countActiveReservationsByCustomer(Long customerId);
}
