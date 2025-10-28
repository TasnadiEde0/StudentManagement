package org.learning.studentManagement.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.sql.DataSource;
import java.nio.file.AccessDeniedException;

@Controller
public class SecurityController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private JdbcUserDetailsManager userDetailsManager;

    @PostConstruct
    public void init() {
        userDetailsManager = new JdbcUserDetailsManager(dataSource);
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public RedirectView register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("password2")  String password2) {

        if(password != null && password.equals(password2)) {
            throw new IllegalArgumentException("Passwords don't match!");
        }

        UserDetails userDetails = User.withUsername(username).password(passwordEncoder.encode(password)).roles("USER").build();

        userDetailsManager.createUser(userDetails);

        return new RedirectView("/login");
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public RedirectView login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("auth", securityContext);

        return new RedirectView("/student");

    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMsg", ex.getMessage());

        return "error";

    }

}
