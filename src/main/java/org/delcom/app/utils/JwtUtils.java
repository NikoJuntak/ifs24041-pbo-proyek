package org.delcom.app.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // Kunci Rahasia (Harus panjang untuk HS256)
    private static final String SECRET_STRING = "KunciRahasiaIniHarusSangatPanjangMinimal32KarakterAgarAman12345";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    
    // Waktu kadaluarsa: 24 Jam
    private final int jwtExpirationMs = 86400000; 

    // 1. Generate Token (Hanya butuh username)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Ambil Username dari Token
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. Validasi Token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT Token: " + e.getMessage());
        }
        return false;
    }
}