package com.trafficlight.jwt;

import com.trafficlight.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Authorization header'ını al
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Token yoksa veya Bearer ile başlamıyorsa devam et
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token'ı çıkar (Bearer prefix'ini kaldır)
        jwt = authHeader.substring(7);
        
        try {
            // Token'dan kullanıcı adını çıkar
            username = jwtService.extractUsername(jwt);

            // Kullanıcı authenticated değilse
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Kullanıcı detaylarını yükle
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Token geçerli mi kontrol et
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Authentication token oluştur
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Security context'e ekle
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token geçersiz veya hatalı - devam et ama authenticate etme
            logger.error("JWT Authentication error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}