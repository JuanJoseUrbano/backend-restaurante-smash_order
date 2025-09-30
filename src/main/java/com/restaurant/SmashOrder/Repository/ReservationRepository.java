package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByStatusIsTrue();
    Optional<Reservation> findByDate(LocalDateTime startDate);
    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId AND r.date = :date AND r.table.status = 'AVAILABLE'")
    List<Reservation> findByTableAndDate(@Param("tableId") Long tableId, @Param("date") LocalDateTime date);
    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId AND r.date = :date AND r.id <> :reservationId AND r.status = true")
    List<Reservation> findByTableAndDateExcludingId(@Param("tableId") Long tableId,
                                                    @Param("date") LocalDateTime date,
                                                    @Param("reservationId") Long reservationId);







}
