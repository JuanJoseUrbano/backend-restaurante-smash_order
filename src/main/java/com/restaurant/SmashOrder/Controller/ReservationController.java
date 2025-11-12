package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.DTO.ReservationDTO;
import com.restaurant.SmashOrder.Entity.Reservation;
import com.restaurant.SmashOrder.IService.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    @GetMapping("/paginated")
    public ResponseEntity<Page<ReservationDTO>> getReservationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reservationService.getReservationsPaginated(page, size));
    }

    @GetMapping("/history/customer/{customerId}")
    public ResponseEntity<?> getReservationHistoryByCustomer(@PathVariable Long customerId) {
        List<ReservationDTO> reservations = reservationService.getOrdersByCustomer(customerId);

        if (reservations.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No reservations found for customer with ID: " + customerId);
        }

        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/date/{date}")
    public ResponseEntity<?> getByDatePaginated(
            @PathVariable String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            LocalDateTime parsedDate = LocalDateTime.parse(date);

            Page<ReservationDTO> reservations = reservationService.getReservationsByDatePaginated(parsedDate, page, size);

            if (reservations.hasContent()) {
                return ResponseEntity.ok(reservations);
            } else {
                return ResponseEntity.status(404).body("No reservations found for date: " + date);
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use: yyyy-MM-ddTHH:mm:ss");
        }
    }

    @GetMapping("/status/paginated")
    public ResponseEntity<Page<ReservationDTO>> getReservationsByStatusPaginated(
            @RequestParam boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reservationService.getReservationsByStatusPaginated(status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<ReservationDTO> reservation = reservationService.getReservationById(id);
        return reservation.isPresent()
                ? ResponseEntity.ok(reservation.get())
                : ResponseEntity.status(404).body("Reservation not found with id: " + id);
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
    @GetMapping("/active/count/customer/{customerId}")
    public ResponseEntity<Long> countActiveReservationsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reservationService.countActiveReservationsByCustomer(customerId));
    }
}
