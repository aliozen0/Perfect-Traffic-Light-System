package com.trafficlight.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private Boolean isAdmin;

    public AuthResponse(String token, Long userId, String username, Boolean isAdmin) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.isAdmin = isAdmin;
    }
}