package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.DTO.ReservationDTO;
import com.restaurant.SmashOrder.Entity.Reservation;
import com.restaurant.SmashOrder.IService.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<ReservationDTO> getAll() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/active")
    public List<ReservationDTO> getActive() {
        return reservationService.getActiveReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<ReservationDTO> reservation = reservationService.getReservationById(id);
        return reservation.isPresent()
                ? ResponseEntity.ok(reservation.get())
                : ResponseEntity.status(404).body("Reservation not found with id: " + id);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getByDate(@PathVariable String date) {
        try {
            LocalDateTime parsedDate = LocalDateTime.parse(date);
            Optional<ReservationDTO> reservation = reservationService.getReservationByDate(parsedDate);
            return reservation.isPresent()
                    ? ResponseEntity.ok(reservation.get())
                    : ResponseEntity.status(404).body("Reservation not found with date: " + date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use: yyyy-MM-ddTHH:mm:ss");
        }
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return reservationService.updateReservation(id, reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return reservationService.deleteReservation(id);
    }
}
