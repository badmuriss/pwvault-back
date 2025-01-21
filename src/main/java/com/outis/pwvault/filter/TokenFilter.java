package com.outis.pwvault.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outis.pwvault.dto.ErrorResponse;
import com.outis.pwvault.util.CryptoUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final CryptoUtil cryptoUtil;

    public TokenFilter(CryptoUtil cryptoUtil) {
        this.cryptoUtil = cryptoUtil;
    }

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/token",
            "/authorize",
            "/callback",
            "/auth/token",
            "/api/public"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = authHeader.substring(7);

            String accessToken = cryptoUtil.decryptWithAES(token);
            request.setAttribute("accessToken",accessToken);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now().toString(),
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "Invalid or expired Token"
            );
            response.getWriter().write(convertObjectToJson(error));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith);
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
