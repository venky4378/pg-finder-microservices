package com.pgfinder.api_gateway.filter;

import com.pgfinder.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Whitelist endpoints that do NOT require authentication
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/v1/auth",
            "/eureka"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Allow browser preflight OPTIONS requests without requiring a token
        if (request.getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = request.getURI().getPath();

        // Check if endpoint is secured (not in whitelist)
        boolean isSecured = OPEN_API_ENDPOINTS.stream().noneMatch(path::startsWith);

        if (isSecured) {
            // 1. Check if Authorization header is present
            if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // 2. Validate token
            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // 3. Extract claims and propagate to downstream services
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Highest priority filter
    }
}