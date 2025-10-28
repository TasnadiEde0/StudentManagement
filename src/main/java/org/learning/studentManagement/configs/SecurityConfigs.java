package org.learning.studentManagement.configs;

import jakarta.annotation.PostConstruct;
import org.learning.studentManagement.filter.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

@EnableWebSecurity
@Configuration
public class SecurityConfigs {

    @Autowired
    private DataSource dataSource;

    private JdbcUserDetailsManager userDetailsManager;

    @PostConstruct
    public void init() {
        userDetailsManager = new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/student")
                )
                .logout(logout ->logout.logoutSuccessUrl("/login"))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/logout").permitAll()
                                .requestMatchers("/register").permitAll()
                                .requestMatchers("/utils/student/add").hasRole("ADMIN")
                                .requestMatchers("/utils/**").hasRole("ADMIN")
                                .requestMatchers("/**").hasRole("USER")
                                .anyRequest().authenticated()
                )
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(daoAuthenticationProvider);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public UserDetailsService userDetailsService() throws SQLException {
//        UserDetails userDetails = User
//                .withUsername("user")
//                .password(passwordEncoder().encode("user"))
//                .roles("USER")
//                .build();
//        UserDetails adminDetails = User
//                .withUsername("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("USER", "ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(userDetails, adminDetails);s

//        userDetailsManager.createUser(userDetails);
//        userDetailsManager.createUser(adminDetails);

        return userDetailsManager;

    }

    @Bean
    public FilterRegistrationBean<LoggingFilter> filterRegistrationBean() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        LoggingFilter loggingFilter = new LoggingFilter();
        registrationBean.setFilter(loggingFilter);
        registrationBean.setOrder(Integer.MIN_VALUE);

        return registrationBean;
    }

}
