package org.learning.studentManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Api controller for testing purposes
 */

@Slf4j
@RestController
@RequestMapping("/api/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String findAll() throws JsonProcessingException {
        List<Group> groups = groupService.findAll();
        return objectMapper.writeValueAsString(groups);
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Integer id) throws JsonProcessingException {
        Group group = groupService.findById(id).orElse(null);
        return objectMapper.writeValueAsString(group);
    }

    @PostMapping
    public String save(@RequestBody Group group) throws JsonProcessingException {
        Group savedGroup = groupService.save(group);
        return objectMapper.writeValueAsString(savedGroup);
    }

    @PutMapping
    public void update(@RequestBody Group group) throws JsonProcessingException {
        groupService.update(group);
    }

    @DeleteMapping
    public void delete(@RequestBody Group group) throws JsonProcessingException {
        groupService.delete(group);
    }
}
