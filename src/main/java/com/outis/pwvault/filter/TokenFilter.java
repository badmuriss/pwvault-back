package com.outis.pwvault.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outis.pwvault.dto.ErrorResponse;
import com.outis.pwvault.exception.CryptoException;
import com.outis.pwvault.exception.UnauthenticatedException;
import com.outis.pwvault.util.CryptoUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {

    @Value("${app.test.profile:false}")
    private boolean isTest;

    private final CryptoUtil cryptoUtil;

    public TokenFilter(CryptoUtil cryptoUtil) {
        this.cryptoUtil = cryptoUtil;
    }

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/authorize",
            "/callback",
            "/api-docs",
            "/swagger-ui",
            "/auth"
    );

    @Value("${PWVAULT_FRONT_URI:http://localhost:8080}")
    private String frontendURI;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (isTest) {
            request.setAttribute("accessToken","token");
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                throw new UnauthenticatedException("No token found");
            }

            String accessToken = cryptoUtil.decryptWithAES(token);

            if (token.equals(accessToken)){
                throw new CryptoException("Invalid token");
            }

            String[] tokenParts = accessToken.split("\\.");
            if (tokenParts.length < 2) {
                throw new SecurityException("Invalid token format");
            }

            String payload = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);

            JSONObject payloadJson = new JSONObject(payload);

            long expirationTime = payloadJson.getLong("exp");
            long currentTimeInSeconds = System.currentTimeMillis() / 1000;

            if (expirationTime < currentTimeInSeconds) {
                throw new UnauthenticatedException("Expired token");
            }

            request.setAttribute("accessToken",accessToken);

        } catch (Exception e) {
            response.setHeader("Location", this.frontendURI + "/token-fail");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now().toString(),
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    e.getMessage()
            );
            response.getWriter().write(convertObjectToJson(error));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        // First try Authorization header (for backward compatibility)
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Then try cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
