package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(String name);
    ResponseEntity<String> createRole(Role role);
    ResponseEntity<String> updateRole(Long id, Role role);
    ResponseEntity<String> deleteRole(Long id);
}
