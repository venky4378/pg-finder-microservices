package com.pgfinder.api_gateway.filter;

import com.pgfinder.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/v1/auth",
            "/api/v1/ai",
            "/eureka"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Allow browser preflight OPTIONS requests without requiring a token
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 2. Anti-Spoofing: Strip any client-supplied identity headers before processing
        ServerHttpRequest cleanRequest = request.mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove("X-User-Id");
                    httpHeaders.remove("X-User-Role");
                    httpHeaders.remove("X-User-Email");
                })
                .build();
        ServerWebExchange cleanExchange = exchange.mutate().request(cleanRequest).build();

        String path = cleanRequest.getURI().getPath();
        HttpMethod method = cleanRequest.getMethod();

        // Public read-only endpoints (anyone can browse hostels, rooms, beds, amenities)
        boolean isPublicRead = method == HttpMethod.GET && (
                path.startsWith("/api/v1/hostels") ||
                        path.startsWith("/api/v1/rooms") ||
                        path.startsWith("/api/v1/beds") ||
                        path.startsWith("/api/v1/amenities")
        );

        boolean isWhitelisted = OPEN_API_ENDPOINTS.stream().anyMatch(path::startsWith) || isPublicRead;

        // 3. Protected Endpoints: Strict JWT validation is required
        if (!isWhitelisted) {
            if (!cleanRequest.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return this.onError(cleanExchange, HttpStatus.UNAUTHORIZED);
            }

            String authHeader = cleanRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(cleanExchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                return this.onError(cleanExchange, HttpStatus.UNAUTHORIZED);
            }

            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            // Inject VERIFIED headers derived from cryptographic JWT
            ServerHttpRequest authenticatedRequest = cleanRequest.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            return chain.filter(cleanExchange.mutate().request(authenticatedRequest).build());
        }

        // 4. Whitelisted Endpoints: If an optional valid token is provided, attach identity
        if (cleanRequest.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
            String authHeader = cleanRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.isTokenValid(token)) {
                    Long userId = jwtUtil.extractUserId(token);
                    String role = jwtUtil.extractRole(token);

                    ServerHttpRequest authenticatedRequest = cleanRequest.mutate()
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", role != null ? role : "")
                            .build();

                    return chain.filter(cleanExchange.mutate().request(authenticatedRequest).build());
                }
            }
        }

        // Forward sanitized request with zero spoofed headers
        return chain.filter(cleanExchange);
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