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

    @Value("${PWVAULT_FRONT_IPV4}")
    private String allowedOriginIPv4;

    @Value("${PWVAULT_FRONT_IPV6}")
    private String allowedOriginIPv6;

    private final String localhostIPv4 = "127.0.0.1"; // IPv4 localhost
    private final String localhostIPv6 = "0:0:0:0:0:0:0:1"; // IPv6 localhost (::1)

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println(request.getLocalAddr());
        System.out.println(request.getRemoteAddr());

        String ipOrigin = request.getRemoteAddr();

        if (ipOrigin == null ||
            (!ipOrigin.equals(allowedOriginIPv4) && !ipOrigin.equals(localhostIPv4)
            && !ipOrigin.equals(allowedOriginIPv6) && !ipOrigin.equals(localhostIPv6))) {

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