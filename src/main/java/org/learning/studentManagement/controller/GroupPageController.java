package org.learning.studentManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.exception.GroupNameDuplicateException;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class GroupPageController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/group")
    public String group(Model model) throws JsonProcessingException {
        List<Group> groups = groupService.findAll();
        model.addAttribute("groups", groups);
        return "group";
    }

    private void groupDuplicateCheck(String name) throws GroupNameDuplicateException {
        Optional<Group> testGroup = groupService.findByName(name);
        if (testGroup.isPresent()) {
            throw new GroupNameDuplicateException();
        }
    }

    @PostMapping("/utils/group/add")
    public RedirectView addGroup(
            @RequestParam("name") String name
    ) {
        Group group = new Group();

        groupDuplicateCheck(name);

        group.setName(name);
        groupService.save(group);

        return new RedirectView("/group"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PostMapping("/utils/group/delete")
    public RedirectView deleteGroup(
            @RequestParam("id") String id
    ) {
        Group group = groupService.findById(Integer.parseInt(id)).orElse(null);
        groupService.delete(group);

        return new RedirectView("/group"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PostMapping("/utils/group/alter")
    public RedirectView alterGroup(
            @RequestParam("id") String id,
            @RequestParam("name") String name
    ) {
        Group group = groupService.findById(Integer.parseInt(id)).orElse(null);
        group.setName(name);
        groupService.update(group);

        return new RedirectView("/group"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

}
