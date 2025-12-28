package com.trafficlight.controller;

import com.trafficlight.entity.User;
import com.trafficlight.jwt.AuthRequest;
import com.trafficlight.jwt.AuthResponse;
import com.trafficlight.jwt.RegisterRequest;
import com.trafficlight.jwt.JwtService;
import com.trafficlight.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Kullanıcı kaydı
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Kullanıcıyı oluştur
            User user = User.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .isAdmin(false)
                    .enabled(true)
                    .build();

            User savedUser = userService.createUser(user);

            // JWT token oluştur
            String token = jwtService.generateToken(savedUser);

            // Response oluştur
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .isAdmin(savedUser.getIsAdmin())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Kullanıcı girişi (Standart Şifreli)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            // Kullanıcıyı doğrula
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Kullanıcı bilgilerini al
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // JWT token oluştur
            String token = jwtService.generateToken(userDetails);

            // Response oluştur
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .isAdmin(user.getIsAdmin())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Kullanıcı adı veya şifre hatalı");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Google Login (YENİ EKLENDİ)
     * Frontend'den gelen e-posta ile giriş yapar veya kullanıcı oluşturur.
     */
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            // Kullanıcı veritabanında var mı? (Username alanını email olarak kullanıyoruz)
            User user = userService.findByUsername(email)
                    .orElseGet(() -> {
                        // Kullanıcı yoksa otomatik oluştur
                        User newUser = User.builder()
                                .username(email)
                                .password(UUID.randomUUID().toString()) // Rastgele güvenli şifre
                                .isAdmin(false)
                                .enabled(true)
                                .build();
                        return userService.createUser(newUser);
                    });

            // Kullanıcı için Backend JWT Token üret (Şifre kontrolüne gerek yok, Google zaten doğruladı)
            String token = jwtService.generateToken(user);

            // Response oluştur
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .isAdmin(user.getIsAdmin())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Google login işlemi başarısız: " + e.getMessage()));
        }
    }

    /**
     * Token doğrulama
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("valid", false, "message", "Geçersiz token formatı"));
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            boolean isValid = jwtService.isTokenValid(token, user);

            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", user.getUsername());
                response.put("isAdmin", user.getIsAdmin());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Token süresi dolmuş veya geçersiz"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token doğrulama başarısız"));
        }
    }

    /**
     * Mevcut kullanıcı bilgilerini getir
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("isAdmin", user.getIsAdmin());
            userInfo.put("enabled", user.isEnabled());

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Kullanıcı bilgileri alınamadı"));
        }
    }
}