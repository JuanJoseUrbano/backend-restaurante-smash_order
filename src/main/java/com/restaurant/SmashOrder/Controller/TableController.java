package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.Entity.TableEntity;
import com.restaurant.SmashOrder.Service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {
    @Autowired
    private TableService tableService;

    @GetMapping
    public List<TableEntity> getAllTables() {
        return tableService.getAllTables();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTableById(@PathVariable Long id) {
        Optional<TableEntity> table = tableService.getTableById(id);
        if (table.isPresent()) {
            return ResponseEntity.ok(table.get());
        } else {
            return ResponseEntity.status(404).body("Table not found with id: " + id);
        }
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<?> getTableByNumber(@PathVariable Integer number) {
        Optional<TableEntity> table = tableService.getTableByNumber(number);
        if (table.isPresent()) {
            return ResponseEntity.ok(table.get());
        } else {
            return ResponseEntity.status(404).body("Table not found with number: " + number);
        }
    }

    @GetMapping("/status/{status}")
    public List<TableEntity> getTablesByStatus(@PathVariable String status) {
        return tableService.getTablesByStatus(status);
    }

    @GetMapping("/capacity")
    public List<TableEntity> getTablesByCapacity(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity
    ) {
        return tableService.getTablesByCapacityRange(minCapacity, maxCapacity);
    }

    @PostMapping
    public ResponseEntity<String> createTable(@RequestBody TableEntity table) {
        return tableService.createTable(table);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTable(@PathVariable Long id, @RequestBody TableEntity table) {
        return tableService.updateTable(id, table);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTable(@PathVariable Long id) {
        return tableService.deleteTable(id);
    }

    @GetMapping("/available")
    public List<TableEntity> getAvailableTablesByCapacity(@RequestParam Integer requiredCapacity) {
        return tableService.getAvailableTablesByCapacity(requiredCapacity);
    }

    @GetMapping("/available/count")
    public Long countAvailableTables() {
        return tableService.countAvailableTables();
    }

    @GetMapping("/exists/{number}")
    public boolean existsByNumber(@PathVariable Integer number) {
        return tableService.existsByNumber(number);
    }
}