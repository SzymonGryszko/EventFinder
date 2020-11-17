package com.gryszko.eventFinder.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@Service
public class JwtProvider {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("authorities", principal.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtConfig.getTokenExpirationTime())))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String jwt) {
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwt);
        return true;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    public String generateTokenWithUsernameAndRole(String username, UserRole userRole) {
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", Set.of(new SimpleGrantedAuthority(userRole.toString())))
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtConfig.getTokenExpirationTime())))
                .signWith(secretKey)
                .compact();
    }
}
