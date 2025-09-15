package co.com.crediya.api;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import co.com.crediya.api.dto.TokenValidationResponseDTO;
import co.com.crediya.api.dto.UserAuthDTO;
import co.com.crediya.api.dto.UserDTO;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMs;
    private final long refreshExpirationMs;
    private final String issuer;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        this.issuer = issuer;
    }

    public String generateAccessToken(String userId){
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userId)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(UserDTO userDTO) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userDTO.id())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .claim("email", userDTO.email())
                .claim("role", userDTO.role())
                .claim("firstName", userDTO.firstName())
                .claim("lastName", userDTO.lastName())
                .claim("documendType", userDTO.documentType())
                .claim("documendId", userDTO.documentId())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateAccessToken(UserAuthDTO user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.id())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .claim("email", user.email())
                .claim("role", user.role())
                .claim("firstName", user.firstName())
                .claim("lastName", user.lastName())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userId)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(refreshExpirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public TokenValidationResponseDTO validateToken(String token) {
        try {
            // acepta token con o sin prefijo Bearer
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Jws<Claims> jws = parseToken(token);
            Claims claims = jws.getBody();

            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            Date exp = claims.getExpiration();
            long expiresInSeconds = (exp.getTime() - System.currentTimeMillis()) / 1000L;

            if (expiresInSeconds < 0) {
                return TokenValidationResponseDTO.invalid("Token expired");
            }

            return TokenValidationResponseDTO.valid(userId, email, role, expiresInSeconds);
        } catch (JwtException | IllegalArgumentException e) {
            return TokenValidationResponseDTO.invalid("Invalid token: " + e.getMessage());
        }
    }
}
