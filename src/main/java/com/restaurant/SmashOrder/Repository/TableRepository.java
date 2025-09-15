package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<TableEntity,Long> {
    Optional<TableEntity> findByNumber(Integer number);
    boolean existsByNumber(Integer number);

    List<TableEntity> findByStatus(String status);

    List<TableEntity> findByCapacityGreaterThanEqual(Integer minCapacity);
    List<TableEntity> findByCapacityLessThanEqual(Integer maxCapacity);
    List<TableEntity> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);

    @Query("SELECT t FROM TableEntity t WHERE t.status = 'AVAILABLE' AND t.capacity >= :requiredCapacity")
    List<TableEntity> findAvailableTablesByCapacity(@Param("requiredCapacity") Integer requiredCapacity);

    @Query("SELECT COUNT(t) FROM TableEntity t WHERE t.status = 'AVAILABLE'")
    Long countAvailableTables();
}
