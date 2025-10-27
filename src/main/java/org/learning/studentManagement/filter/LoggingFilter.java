package org.learning.studentManagement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
//@Component
public class LoggingFilter implements Filter {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response, FilterChain chain
    ) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        chain.doFilter(request, response);

        log.info("{} {} {}", req.getMethod(), req.getRequestURI(), resp.getStatus());
        log.info(objectMapper.writeValueAsString(req.getParameterMap()));

    }
}
