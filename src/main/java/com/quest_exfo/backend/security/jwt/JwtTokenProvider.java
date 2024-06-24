// package com.quest_exfo.backend.security.jwt;


// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import jakarta.annotation.PostConstruct;


// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Component;

// import javax.crypto.SecretKey;
// import java.util.Base64;
// import java.util.Date;
// import java.util.logging.*;

// @Component
// public class JwtTokenProvider {

//     @Value("${security.jwt.token.secret-key}")
//     private String secretKey;

//     @Value("${security.jwt.token.access-token-expiration}")
//     private long validityInMilliseconds;

//     @Value("${security.jwt.token.refresh-token-expiration}")
//     private long refreshTokenValidityMs;

//     private SecretKey key;
//     private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

//     @PostConstruct
//     protected void init() {
//         // Use the secret key from the properties to create a valid SecretKey for HS256
//         this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
//     }

//     //토큰
//     public String createToken(String username, String role){
//         Claims claims = Jwts.claims().setSubject(username);
//         claims.put("role",role);

//         Date now = new Date();
//         Date validity = new Date(now.getTime()+validityInMilliseconds); //만료시간

//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setIssuedAt(now)
//                 .setExpiration(validity)
//                 .signWith(key, SignatureAlgorithm.HS256)// HMAC-SHA256으로 서명
//                 .compact();
//     }

//     //리프레시토큰
//     public String generateRefreshToken(String username){
//         Date now = new Date();
//         Date expiryDate = new Date(now.getTime()+refreshTokenValidityMs); //만료시간

//         return Jwts.builder()
//                 .setSubject(username)
//                 .setIssuedAt(new Date())
//                 .setExpiration(expiryDate)
//                 .signWith(key, SignatureAlgorithm.HS256)// HMAC-SHA256으로 서명 => 보안강화
//                 .compact();
//     }


//     public boolean validateToken(String token) {
//         try {
//             Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//             return true;
//         } catch (Exception e) {
//             logger.warning("JWT validation failed: " + e.getMessage());
//             return false;
//         }
//     }

//     public String getUsername(String token){
//         return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
//     }

//     public String getRole(String token) {
//         return (String) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role");
//     }

// }


package com.quest_exfo.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class);

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.access-token-expiration}")
    private long validityInMilliseconds;

    @Value("${security.jwt.token.refresh-token-expiration}")
    private long refreshTokenValidityMs;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        // Use the secret key from the properties to create a valid SecretKey for HS256
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // 만료시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256) // HMAC-SHA256으로 서명
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityMs); // 만료시간

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) // HMAC-SHA256으로 서명 => 보안강화
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Token validation error: ", e);
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            logger.error("Error getting username from token: ", e);
            return null;
        }
    }

    public String getRole(String token) {
        try {
            return (String) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role");
        } catch (Exception e) {
            logger.error("Error getting role from token: ", e);
            return null;
        }
    }
}
