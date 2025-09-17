package com.restaurant.SmashOrder.DTO;

import com.restaurant.SmashOrder.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String userName;
    private String name;
    private String email;
    private Set<Role> roles;
}
