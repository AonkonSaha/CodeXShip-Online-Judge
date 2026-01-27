package com.judge.myojudge.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.controller.AuthController;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.imp.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private  JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthController authController;

    public JwtAuthFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            String authHeader = request.getHeader("Authorization");
            String requestURI = request.getRequestURI();
            System.out.println("I am in filter: "+requestURI);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractUserEmail(token);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                if (jwtUtil.validateToken(token,userEmail)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        }catch (ExpiredJwtException e) {
            sendJwtError(response, request, "JWT token has expired");

        } catch (SignatureException e) {
            sendJwtError(response, request, "Invalid JWT signature");

        } catch (MalformedJwtException e) {
            sendJwtError(response, request, "Malformed JWT token");

        } catch (Exception e) {
            sendJwtError(response, request, "Invalid JWT token");
        }

    }
    private void sendJwtError(
            HttpServletResponse response,
            HttpServletRequest request,
            String message
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("statusCode", 401);
        body.put("message", message);
        body.put("path", request.getRequestURI());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }


}
