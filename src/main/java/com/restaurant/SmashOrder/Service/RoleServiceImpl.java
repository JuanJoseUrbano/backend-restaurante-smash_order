package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Role;
import com.restaurant.SmashOrder.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public ResponseEntity<String> createRole(Role role) {
        try {
            validateRole(role);
            if (roleRepository.findByName(role.getName()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Role with name '" + role.getName() + "' already exists");
            }
            roleRepository.save(role);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Role created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating role: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateRole(Long id, Role role) {
        try {
            Role existingRole = roleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

            validateRole(role);
            existingRole.setName(role.getName());

            roleRepository.save(existingRole);
            return ResponseEntity.ok("Role updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating role: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
        return ResponseEntity.ok("Role deleted successfully");
    }

    private void validateRole(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new RuntimeException("Role name is required");
        }
    }
}
