package com.example.Vehicles.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey; // Secret key – do podpisywania tokenu
    @Value("${jwt.expiration}")
    private long expirationMs; // czas ważności tokenu

    //Metoda do generowania tokenu:
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .header().type("JWT").and()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .claim("role", getUserRole(userDetails)) // dodajemy rolę usera
                .signWith(getSigningKey())
                .compact();
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject(); //pobranie nazwy usera z tokenu
    }

    //Walidacja tokenu – czy Username i podpis się zgadza, oraz czy token nie wygasł
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);

            if (!username.equals(userDetails.getUsername())) {
                return false;
            }
            return !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        Jws<Claims> jwsClaims = Jwts.parser() //parser tokena
                .verifyWith(getSigningKey()) //SPRWADZANIE PODPISU TOKENA!!
                .build() //budowanie parsera
                .parseSignedClaims(token); //Gdy podpis prawidłowy sparoswanie tokena
        return jwsClaims.getPayload(); // zwrócenie claims (body) – dane usera – ciało tokenu.
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey); //dekoduje secretKey z base64url z pliku
        return Keys.hmacShaKeyFor(keyBytes); // generuje SecretKey dla algorytmuHMAC-SHA
// HMAC to metoda szyfrowania, która wykorzystuje funkcję skrótu (np. SHA-256) i klucz tajny do generowania podpisu
    }

    private String getUserRole(UserDetails userDetails) { //Zwraca role usera
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }

    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }
}
