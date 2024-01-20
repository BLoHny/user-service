package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final CustomAuthenticationManager customAuthenticationManager;
    private final UserService userService;
    private final Environment environment;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
/*        http
                .formLogin().disable();*/
        http.authorizeHttpRequests()
                .requestMatchers("/**").permitAll()
                .requestMatchers(new IpAddressMatcher("127.0.0.1")).permitAll();

        http
                .addFilter(getAuthenticationFilter());
        return http.build();
    }

    public AuthenticationFilter getAuthenticationFilter() {
        return new AuthenticationFilter(customAuthenticationManager, userService, environment);
    }
}
