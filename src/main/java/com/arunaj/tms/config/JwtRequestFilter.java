package com.arunaj.tms.config;

import com.arunaj.tms.service.AccountService;
import com.arunaj.tms.util.JwtUtil;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerUtil.getLogger(JwtRequestFilter.class);
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("received request: " + request.getRequestURI());
        String headerAuth = request.getHeader("Authorization");
        String username;
        String jwtToken;

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwtToken = headerAuth.substring(7);

            if (jwtUtil.validateToken(jwtToken)) {
                username = jwtUtil.getUsernameFromToken(jwtToken);

                UserDetails userDetails = accountService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        filterChain.doFilter(request, response);
    }
}
