package com.judge.myojudge.jwt;

import com.judge.myojudge.model.User;
import com.judge.myojudge.repo.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private  JwtUtil jwtUtil;
    @Autowired
    private   UserDetailsService userDetailsService;
    @Autowired
    private UserRepo userRepo;

    public JwtAuthFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        System.out.println(" "+requestURI);
//        if (requestURI.startsWith("/api/")) {
//            filterChain.doFilter(request, response); // Skip JWT validation for public endpoints
//            return;
//        }
        System.out.println("I am in Filter");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("ami khane");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        System.out.println("token: "+token);


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
           User user=userRepo.findByUsername(username).orElseThrow();
            if (user.getActivityStatus() && jwtUtil.validateToken(token, username)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
