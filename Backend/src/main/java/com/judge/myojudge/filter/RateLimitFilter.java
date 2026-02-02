package com.judge.myojudge.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.myojudge.response.ErrorResponse;
import com.judge.myojudge.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class RateLimitFilter extends OncePerRequestFilter {
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("I am a Rate Limit Filter...");

        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (response.isCommitted()) {
            return;
        }

        response.setContentType("application/json");
            response.setStatus(429);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
                    .message("Too many requests")
                    .path(request.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        System.out.println("----------TOO MANY Request..................>>>>>");

            return;

    }
}
