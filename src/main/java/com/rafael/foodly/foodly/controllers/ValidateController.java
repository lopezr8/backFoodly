package com.rafael.foodly.foodly.controllers;

import static com.rafael.foodly.foodly.security.TokenJwtConfig.PREFIX_TOKEN;
import static com.rafael.foodly.foodly.security.TokenJwtConfig.SECRET_KEY;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael.foodly.foodly.security.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ValidateController {

    @SuppressWarnings("deprecation")
    @GetMapping("/validate-token")
    public Map<String, Object> validateToken(@RequestHeader("Authorization") String header) {
        Map<String, Object> response = new HashMap<>();
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            response.put("valid", false);
            response.put("message", "Missing or invalid token prefix");
            return response;
        }

        String token = header.replace(PREFIX_TOKEN, "");
        try {



            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();

            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                new ObjectMapper()
                .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                .readValue(claims.get("authorities").toString().getBytes(), SimpleGrantedAuthority[].class)
                );

            
            
            response.put("valid", true);
            response.put("username", claims.getSubject());
            response.put("authorities", authorities );

            return response;
        } catch (SignatureException e) {
            response.put("valid", false);
            response.put("message", "Invalid token signature");
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Token validation failed: " + e.getMessage());
        }

        return response;
    }

}
