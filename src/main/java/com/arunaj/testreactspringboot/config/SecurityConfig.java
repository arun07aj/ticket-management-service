package com.arunaj.testreactspringboot.config;

import com.arunaj.testreactspringboot.service.AccountService;
import com.arunaj.testreactspringboot.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerUtil.getLogger(SecurityConfig.class);
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService).passwordEncoder(new BCryptPasswordEncoder());
        logger.info("authentication manager build completed");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                // skip auth for public api
                .authorizeRequests().antMatchers("/api/public/*", "/h2-console/**").permitAll()
//                .antMatchers("/tickets/list/").hasAuthority("ADMIN")
//                .antMatchers("/").hasAuthority("USER")
                .antMatchers("/list/**").access("hasAuthority('ADMIN') or hasPermission(#id, 'com.arunaj.testreactspringboot.model.Ticket', 'READ')")
                .antMatchers("/tickets/create", "/tickets/edit/*", "/tickets/list/my").hasAnyAuthority("USER", "ADMIN")
                // auth all other requests
                .anyRequest().authenticated()
                .and()
                // for handling unauthorized user exception
                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                // if stateful then no auth until its expiry
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.headers().frameOptions().sameOrigin();
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}