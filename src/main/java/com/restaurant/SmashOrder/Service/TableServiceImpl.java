package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.TableEntity;
import com.restaurant.SmashOrder.Repository.TableRepository;
import com.restaurant.SmashOrder.IService.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {
    private final TableRepository tableRepository;
    @Override
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }
    @Override
    public Page<TableEntity> getAllTablesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return tableRepository.findAll(pageable);
    }

    @Override
    public Page<TableEntity> getTablesByStatusPaginated(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return tableRepository.findByStatus(status, pageable);
    }

    @Override
    public List<TableEntity> getTablesByStatus(String status) {
        return tableRepository.findByStatus(status);
    }

    @Override
    public Optional<TableEntity> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    @Override
    public Optional<TableEntity> getTableByNumber(Integer number) {
        return tableRepository.findByNumber(number);
    }

    @Override
    public List<TableEntity> getTablesByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        if (minCapacity != null && maxCapacity != null) {
            return tableRepository.findByCapacityBetween(minCapacity, maxCapacity);

        } else if (minCapacity != null) {
            return tableRepository.findByCapacityGreaterThanEqual(minCapacity);

        } else if (maxCapacity != null) {
            return tableRepository.findByCapacityLessThanEqual(maxCapacity);
        }
        return tableRepository.findAll();
    }

    @Override
    public ResponseEntity<String> createTable(TableEntity table) {
        try {
            validateTable(table);

            if (tableRepository.existsByNumber(table.getNumber())) {
                return ResponseEntity.badRequest().body("Error: Ya existe una mesa con ese número.");
            }

            tableRepository.save(table);
            return ResponseEntity.ok("Mesa creada exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al crear la mesa: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateTable(Long id, TableEntity tableDetails) {
        try {
            TableEntity existingTable = tableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
            validateTable(tableDetails);

            // Verificar que el número no esté en uso por otra mesa
            if (!existingTable.getNumber().equals(tableDetails.getNumber()) &&
                    tableRepository.existsByNumber(tableDetails.getNumber())) {
                throw new RuntimeException("Table number already in use: " + tableDetails.getNumber());
            }

            existingTable.setNumber(tableDetails.getNumber());
            existingTable.setCapacity(tableDetails.getCapacity());
            existingTable.setStatus(tableDetails.getStatus());

            tableRepository.save(existingTable);

            return ResponseEntity.ok("Table updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating table: " + e.getMessage());
        }
    }


    @Override
    public ResponseEntity<String> deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tableRepository.deleteById(id);
        return ResponseEntity.ok("Mesa eliminada exitosamente.");
    }

    @Override
    public List<TableEntity> getAvailableTablesByCapacity(Integer requiredCapacity) {
        return tableRepository.findAvailableTablesByCapacity(requiredCapacity);
    }

    @Override
    public Long countAvailableTables() {
        return tableRepository.countAvailableTables();
    }

    @Override
    public boolean existsByNumber(Integer number) {
        return tableRepository.existsByNumber(number);
    }

    private void validateTable(TableEntity table) {
        if (table == null) {
            throw new RuntimeException("Table cannot be null");
        }

        if (table.getNumber() == null || table.getNumber() <= 0) {
            throw new RuntimeException("Table number must be greater than zero");
        }

        if (table.getCapacity() == null || table.getCapacity() <= 0) {
            throw new RuntimeException("Table capacity must be greater than zero");
        }

        if (table.getStatus() != null && table.getStatus().trim().isEmpty()) {
            throw new RuntimeException("Table status cannot be empty if provided");
        }
    }
}
