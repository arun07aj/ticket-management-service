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
import org.springframework.web.filter.CorsFilter;

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
    private CorsConfig corsConfig;
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
                .cors()
                .and()
                .addFilterBefore(corsConfig.corsFilter(), CorsFilter.class)
                // skip auth for public api
                .authorizeRequests().antMatchers("/api/public/*", "/h2-console/**").permitAll()
                // api requiring auth and roles
                .antMatchers("/tickets/list/").hasAuthority("ADMIN")
                .antMatchers("/tickets/list/{id}").hasAnyAuthority("USER", "ADMIN")
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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().disable();

        http.headers().frameOptions().sameOrigin();
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}