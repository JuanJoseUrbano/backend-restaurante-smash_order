package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.DTO.ReservationDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.Reservation;
import com.restaurant.SmashOrder.Entity.TableEntity;
import com.restaurant.SmashOrder.IService.ReservationService;
import com.restaurant.SmashOrder.Repository.ReservationRepository;
import com.restaurant.SmashOrder.Repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;

    @Override
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReservationDTO> getReservationsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return reservationRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Override
    public Page<ReservationDTO> getReservationsByStatusPaginated(boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return reservationRepository.findByStatus(status, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public Optional<ReservationDTO> getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public Page<ReservationDTO> getReservationsByDatePaginated(LocalDateTime date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return reservationRepository.findByDate(date, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public ResponseEntity<String> createReservation(Reservation reservation) {
        try {
            if (!validateReservationDate(reservation.getDate())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid reservation dates: start date must be in the future.");
            }

            reservation.setStatus(true);

            TableEntity table = tableRepository.findById(reservation.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            if (checkAvailability(reservation)) {
                table.setStatus("RESERVED");
                tableRepository.save(table);

                reservation.setTable(table);
                reservationRepository.save(reservation);

                return ResponseEntity.ok("Reservation created successfully");
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The table is not available at the requested time.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating reservation: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateReservation(Long id, Reservation reservation) {
        if (!validateReservationDate(reservation.getDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid reservation dates: start date must be in the future.");
        }

        Optional<Reservation> existingReservationOpt = reservationRepository.findById(id);
        if (existingReservationOpt.isPresent()) {
            Reservation existingReservation = existingReservationOpt.get();

            TableEntity table = tableRepository.findById(reservation.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            Reservation updatedReservation = new Reservation();
            updatedReservation.setId(id);
            updatedReservation.setCustomer(reservation.getCustomer());
            updatedReservation.setTable(table);
            updatedReservation.setDate(reservation.getDate());
            updatedReservation.setStatus(reservation.getStatus());

            if (checkAvailability(updatedReservation)) {
                // Liberar mesa anterior si cambió de mesa
                if (!existingReservation.getTable().getId().equals(table.getId())) {
                    TableEntity oldTable = tableRepository.findById(existingReservation.getTable().getId())
                            .orElseThrow(() -> new RuntimeException("Old table not found"));
                    oldTable.setStatus("AVAILABLE");
                    tableRepository.save(oldTable);
                }

                table.setStatus("RESERVED");
                tableRepository.save(table);

                reservationRepository.save(updatedReservation);
                return ResponseEntity.ok("Reservation updated successfully");
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The table is not available at the requested time.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Reservation with ID " + id + " not found");
    }

    @Override
    public ResponseEntity<String> deleteReservation(Long id) {
        Optional<Reservation> existingReservation = reservationRepository.findById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();

            TableEntity table = tableRepository.findById(reservation.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));
            table.setStatus("AVAILABLE");
            tableRepository.save(table);

            reservationRepository.delete(reservation);

            return ResponseEntity.ok("Reservation deleted successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Reservation with ID " + id + " not found");
    }


    @Override
    public Long countActiveReservationsByCustomer(Long customerId) {
        return reservationRepository.countActiveReservationsByCustomer(customerId);
    }

    private boolean validateReservationDate(LocalDateTime startDate) {
        return startDate != null && startDate.isAfter(LocalDateTime.now());
    }

    private boolean checkAvailability(Reservation reservation) {
        if (reservation == null || reservation.getTable() == null) {
            throw new IllegalArgumentException("Reservation or table cannot be null");
        }

        List<Reservation> existingReservations;
        if (reservation.getId() != null) {
            // Excluir la propia reserva al actualizar
            existingReservations = reservationRepository.findByTableAndDateExcludingId(
                    reservation.getTable().getId(),
                    reservation.getDate(),
                    reservation.getId());
        } else {
            existingReservations = reservationRepository.findByTableAndDate(
                    reservation.getTable().getId(),
                    reservation.getDate());
        }

        if (!existingReservations.isEmpty()) {
            return false;
        }

        TableEntity table = tableRepository.findById(reservation.getTable().getId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // Permitir actualizar aunque la mesa esté RESERVED si es la propia reserva
        if (reservation.getId() != null) {
            Optional<Reservation> existing = reservationRepository.findById(reservation.getId());
            if (existing.isPresent() && existing.get().getTable().getId().equals(table.getId())) {
                return true;
            }
        }

        return table.getStatus().equalsIgnoreCase("AVAILABLE");
    }

    private ReservationDTO mapToDTO(Reservation reservation) {
        UserDTO customerDTO = new UserDTO(
                reservation.getCustomer().getId(),
                reservation.getCustomer().getUserName(),
                reservation.getCustomer().getName(),
                reservation.getCustomer().getEmail(),
                reservation.getCustomer().getRoles()
        );

        return new ReservationDTO(
                reservation.getId(),
                customerDTO,
                reservation.getTable(),
                reservation.getDate(),
                reservation.getStatus()
        );
    }
}
