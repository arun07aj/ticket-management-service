package com.arunaj.tms.util;

import com.arunaj.tms.model.Account;
import com.arunaj.tms.model.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    String jwtSecret;

    @Value("${jwt.token.validity}")
    long jwtValidity;

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(Account account) {
        String username = account.getUsername();
        AccountRole authorities = account.getRole();

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", authorities.name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidity))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            logger.info("JWT Token OK");
            return true;
        } catch (MalformedJwtException ex) {
            logger.info("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            logger.info("JWT Token has expired");
        } catch (UnsupportedJwtException ex) {
            logger.info("Unsupported JWT Token");
        } catch (IllegalArgumentException ex) {
            // JWT claims string is empty
            logger.info("Unable to get JWT Token");
        }
        catch (Exception e) {
            logger.error("Exception occurred: " + e);
        }
        return false;
    }
}
