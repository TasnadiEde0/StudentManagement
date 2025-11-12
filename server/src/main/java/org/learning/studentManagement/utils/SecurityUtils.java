package org.learning.studentManagement.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;

public class SecurityUtils {
    public static void addAuthsAndNameToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> auths = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(authority -> authority.equals("ROLE_ADMIN")).toList();
        model.addAttribute("auths", auths);

        String username = authentication.getName();
        model.addAttribute("loggedInUsername", username);
    }
}
