package com.sorted.commons.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Log4j2
@Component
@Order(1)
public class CorsDebugFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");
        String referer = httpRequest.getHeader("Referer");
        String method = httpRequest.getMethod();
        String requestURI = httpRequest.getRequestURI();

        log.info("=== CORS DEBUG ===");
        log.info("Method: {}", method);
        log.info("URI: {}", requestURI);
        log.info("Origin: '{}'", origin);
        log.info("Referer: '{}'", referer);
        log.info("User-Agent: {}", httpRequest.getHeader("User-Agent"));

        // Log all headers for OPTIONS requests
        if ("OPTIONS".equals(method)) {
            log.info("=== OPTIONS REQUEST HEADERS ===");
            httpRequest.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                log.info("{}: {}", headerName, httpRequest.getHeader(headerName));
            });
        }

        chain.doFilter(request, response);

        // Log response headers for OPTIONS requests
        if ("OPTIONS".equals(method)) {
            log.info("=== OPTIONS RESPONSE HEADERS ===");
            httpResponse.getHeaderNames().forEach(headerName -> {
                log.info("{}: {}", headerName, httpResponse.getHeader(headerName));
            });
        }
        log.info("=== END CORS DEBUG ===");
    }
}