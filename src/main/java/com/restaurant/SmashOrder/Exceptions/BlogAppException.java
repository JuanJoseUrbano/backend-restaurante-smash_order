package com.restaurant.SmashOrder.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogAppException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private HttpStatus estado;
    private String mensaje;
}
