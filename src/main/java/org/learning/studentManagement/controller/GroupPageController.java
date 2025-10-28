package org.learning.studentManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@Controller
public class GroupPageController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/group")
    public String group(Model model, HttpServletRequest request) throws JsonProcessingException {
        List<Group> groups = groupService.findAll();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("admin", isAdmin);

        model.addAttribute("groups", groups);
        return "group";
    }

    @PostMapping("/utils/group/add")
    public RedirectView addGroup(
            @RequestParam(value = "name") String name
    ) {
        groupService.save(name);

        return new RedirectView("/group"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PostMapping("/utils/group/delete")
    public RedirectView deleteGroup(
            @RequestParam(value = "id") String id
    ) {
        groupService.delete(id);

        return new RedirectView("/group"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PostMapping("/utils/group/alter")
    public RedirectView alterGroup(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name
    ) {
        groupService.update(id, name);

        return new RedirectView("/group"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

}
