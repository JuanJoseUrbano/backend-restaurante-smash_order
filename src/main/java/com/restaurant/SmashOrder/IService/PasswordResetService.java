package com.restaurant.SmashOrder.IService;

public interface PasswordResetService {
    void sendResetLink(String email);
    boolean resetPassword(String token, String newPassword);
}
