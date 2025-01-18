package com.outis.pwvault.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outis.pwvault.dto.ErrorResponse;
import com.outis.pwvault.model.JwtPayload;
import com.outis.pwvault.service.JwtService;
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

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = authHeader.substring(7);

            System.out.println("test");
            JwtPayload payload = jwtService.getDecryptedPayload(token);
            request.setAttribute("accessToken", payload.keyVaultToken());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now().toString(),
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "Invalid or expired JWT"
            );
            response.getWriter().write(convertObjectToJson(error));
            return;
        }
        filterChain.doFilter(request, response);
    }


    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
