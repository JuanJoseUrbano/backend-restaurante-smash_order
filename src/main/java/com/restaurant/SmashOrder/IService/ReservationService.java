package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.ReservationDTO;
import com.restaurant.SmashOrder.Entity.Reservation;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<ReservationDTO> getAllReservations();
    List<ReservationDTO> getActiveReservations();
    Optional<ReservationDTO> getReservationById(Long id);
    Optional<ReservationDTO> getReservationByDate(LocalDateTime date);
    ResponseEntity<String> createReservation(Reservation reservation);
    ResponseEntity<String> updateReservation(Long id, Reservation reservation);
    ResponseEntity<String> deleteReservation(Long id);
    Long countActiveReservationsByCustomer(Long customerId);
}
