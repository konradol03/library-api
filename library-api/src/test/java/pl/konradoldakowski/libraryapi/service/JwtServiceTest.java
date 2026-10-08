package pl.konradoldakowski.libraryapi.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;


public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=", 3_600_000L);
    }
    @Test
    public void shouldGenerateToken() {
        UserDetails userDetails = User.builder().username("test@test.com").password("hashedPassword").roles("USER").build();

        String token = jwtService.generateToken(userDetails);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());
        Assertions.assertEquals("test@test.com",jwtService.extractUsername(token));
    }
    @Test
    public void shouldReturnTrueForValidToken() {
        UserDetails userDetails = User.builder().username("test@test.com").password("hashedPassword").roles("USER").build();

        String token = jwtService.generateToken(userDetails);

        Assertions.assertTrue(jwtService.isTokenValid(token, userDetails));
    }
    @Test
    public void shouldReturnFalseForDifferentUser() {
        UserDetails userDetails = User.builder().username("test@test.com").password("hashedPassword").roles("USER").build();
        UserDetails userDetails1 = User.builder().username("biznes@admin.com").password("unhashedPassword").roles("ADMIN").build();

        String token = jwtService.generateToken(userDetails);

        Assertions.assertFalse(jwtService.isTokenValid(token, userDetails1));
    }
    @Test
    public void shouldRejectExpiredToken() {
        UserDetails userDetails = User.builder().username("test@test.com").password("hashedPassword").roles("USER").build();
        SecretKey sc = Keys.hmacShaKeyFor(Decoders.BASE64.decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="));

        String expiredToken = Jwts.builder().subject(userDetails.getUsername()).issuedAt(new Date(System.currentTimeMillis() - 7_200_000L))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000L))
                .signWith(sc)
                .compact();

        Assertions.assertThrows(ExpiredJwtException.class,() -> jwtService.isTokenValid(expiredToken, userDetails));
    }
}
