package com.outis.pwvault.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.outis.pwvault.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class IpValidationFilter extends OncePerRequestFilter {

    @Value("${app.test.profile:false}")
    private boolean isTest;

    @Value("${PWVAULT_FRONT_IPV4:127.0.0.1}")
    private String allowedOriginIPv4;

    @Value("${PWVAULT_FRONT_IPV6:0:0:0:0:0:0:0:1}")
    private String allowedOriginIPv6;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isTest) {
            filterChain.doFilter(request, response);
            return;
        }

        String ipOrigin = request.getRemoteAddr();

        System.out.println(ipOrigin);

        if (ipOrigin == null ||
            (!ipOrigin.equals(allowedOriginIPv4) && !ipOrigin.equals(allowedOriginIPv6))) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now().toString(),
                    String.valueOf(HttpStatus.FORBIDDEN.value()),
                    HttpStatus.FORBIDDEN.getReasonPhrase(),
                    "Origin not allowed"
            );
            response.getWriter().write(convertObjectToJson(error));
            return;

        }

        filterChain.doFilter(request, response);
    }

    private String convertObjectToJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}