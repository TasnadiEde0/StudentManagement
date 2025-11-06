package org.learning.studentManagement.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;

@EnableMethodSecurity
@Configuration
@Slf4j
public class SecurityConfigs {

    @Autowired
    private Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            PersistentTokenRepository persistentTokenRepository,
            JdbcUserDetailsManager jdbcUserDetailsManager
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/login").permitAll()
                                        .requestMatchers("/logout").permitAll()
                                        .requestMatchers("/register").permitAll()
                                        .requestMatchers("/styles.css").permitAll()
                                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login")
                        )
                )
                .rememberMe(rememberMe ->
                        rememberMe
                                .tokenRepository(persistentTokenRepository)
                                .userDetailsService(jdbcUserDetailsManager)
                                .tokenValiditySeconds(7*24*60*60)
                                .key(environment.getProperty("learning.security.key"))
                                .rememberMeParameter("remember-me")
                )
                .build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        jdbcTokenRepositoryImpl.setDataSource(dataSource);
        return jdbcTokenRepositoryImpl;
    }

    @Bean
    public RememberMeServices rememberMeServices(
            PersistentTokenRepository persistentTokenRepository,
            JdbcUserDetailsManager jdbcUserDetailsManager
    ) {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
                environment.getProperty("learning.security.key"), jdbcUserDetailsManager, persistentTokenRepository);
        rememberMeServices.setAlwaysRemember(false);
        return rememberMeServices;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // also bean for userDetailsService
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

}
