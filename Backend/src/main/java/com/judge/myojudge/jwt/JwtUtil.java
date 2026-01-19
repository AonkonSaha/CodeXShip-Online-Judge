package com.judge.myojudge.jwt;


import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public String generateToken(User user,String email, boolean isActive) {
        String username;
        if(user.getMobileNumber()!=null && user.getMobileNumber().length()>0){
            username = user.getMobileNumber();
        }else{
            username = user.getEmail();
        }
        List<String> roles=new ArrayList<>();
        for(UserRole userRole:user.getUserRoles()){
            roles.add(userRole.getRoleName());
        }

        Map<String,Object>claims=new HashMap<>();
        claims.put("active",isActive);
        claims.put("role",roles);
        claims.put("image_url",user.getImageUrl());
        claims.put("user_id",user.getId());
        claims.put("present_coins",user.getTotalPresentCoins());
        claims.put("is_add_daily_coin", user.getIsAdditionDailyCoin());
        claims.put("daily_reward_coin",5);
        claims.put("num_of_days_login", user.getNumOfDaysLogin());
       return createToken(claims,username);
    }
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
            final Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token))) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
    public Boolean extractActiveStatus(String token) {
        return extractClaim(token, claims -> claims.get("active", Boolean.class));
    }
}
