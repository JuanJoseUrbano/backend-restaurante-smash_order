package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.Entity.Role;
import com.restaurant.SmashOrder.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        if (role.isPresent()) {
            return ResponseEntity.ok(role.get());
        } else {
            return ResponseEntity.status(404).body("Role not found with id: " + id);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRoleByName(@PathVariable String name) {
        Optional<Role> role = roleService.getRoleByName(name);
        if (role.isPresent()) {
            return ResponseEntity.ok(role.get());
        } else {
            return ResponseEntity.status(404).body("Role not found with name: " + name);
        }
    }

    @PostMapping
    public ResponseEntity<String> createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }
}
