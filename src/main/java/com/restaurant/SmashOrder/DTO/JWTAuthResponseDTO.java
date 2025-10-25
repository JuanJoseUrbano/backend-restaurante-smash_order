package com.restaurant.SmashOrder.DTO;

import com.restaurant.SmashOrder.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTAuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer ";
    private UserDTO user;

    public JWTAuthResponseDTO(String tokenDeAcceso) {
        super();
        this.accessToken = tokenDeAcceso;
    }
}
