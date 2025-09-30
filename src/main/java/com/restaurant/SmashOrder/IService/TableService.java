package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.Entity.TableEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TableService {
    List<TableEntity> getAllTables();
    Optional<TableEntity> getTableById(Long id);
    Optional<TableEntity> getTableByNumber(Integer number);

    List<TableEntity> getTablesByStatus(String status);
    List<TableEntity> getTablesByCapacityRange(Integer minCapacity, Integer maxCapacity);

    ResponseEntity<String> createTable(TableEntity table);
    ResponseEntity<String> updateTable(Long id, TableEntity table);
    ResponseEntity<String> deleteTable(Long id);

    List<TableEntity> getAvailableTablesByCapacity(Integer requiredCapacity);
    Long countAvailableTables();
    boolean existsByNumber(Integer number);
}
