package org.learning.studentManagement;

import org.junit.jupiter.api.Test;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GroupServiceTests {
    @Autowired
    private GroupService groupService;

    @MockitoBean
    private GroupDao groupDao;

    private static Group createMockGroup() {
        Group group = new Group();
        group.setId(1);
        group.setName("groupName");

        return group;
    }

    @Test
    public void findAll_isOk() throws Exception {
        //setup
        when(groupDao.findAll()).thenReturn(new ArrayList<>());

        //execute
        assertEquals(new ArrayList<>(), groupService.findAll());
        verify(groupDao, times(1)).findAll();

        //cleanup
    }

    @Test
    public void findById_isOk() throws Exception {
        //setup
        Group group = createMockGroup();
        when(groupDao.findById(1)).thenReturn(Optional.of(group));

        //execute
        assertEquals(group, groupService.findById(1));
        verify(groupDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void findById_incorrectId_throwsException() throws Exception {
        //setup
        when(groupDao.findById(1)).thenReturn(Optional.empty());

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () -> groupService.findById(1));
        assertEquals("The given ID isn't associated with a group!", exception.getMessage());
        verify(groupDao, times(1)).findById(1);

        //cleanup
    }

    @Test
    public void findByName_isOk() throws Exception {
        //setup
        Optional<Group> groupOptional = Optional.of(createMockGroup());
        when(groupDao.findByName("groupName")).thenReturn(groupOptional);

        //execute
        assertEquals(groupOptional, groupService.findByName("groupName"));
        verify(groupDao, times(1)).findByName("groupName");

        //cleanup
    }

    @Test
    public void findByName_incorrectName_isOk() throws Exception {
        //setup
        when(groupDao.findByName("notGroupName")).thenReturn(Optional.empty());

        //execute
        assertEquals(Optional.empty(), groupService.findByName("notGroupName"));
        verify(groupDao, times(1)).findByName("notGroupName");

        //cleanup
    }

    @Test
    public void save_isOk() throws Exception {
        //setup
        Group group = new Group();
        group.setName("groupName");
        Group groupAfterSave = createMockGroup();
        when(groupDao.save(group)).thenReturn(groupAfterSave);
        when(groupDao.findByName("groupName")).thenReturn(Optional.empty());

        //execute
        assertEquals(groupAfterSave, groupService.save("groupName"));
        verify(groupDao, times(1)).save(group);
        verify(groupDao, times(1)).findByName("groupName");

        //cleanup
    }

    @Test
    public void save_duplicateName_throwsException() throws Exception {
        //setup
        Group group = new Group();
        group.setName("groupName");
        when(groupDao.findByName("groupName")).thenReturn(Optional.of(group));

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () -> groupService.save("groupName"));
        assertEquals("Group name already taken!", exception.getMessage());
        verify(groupDao, never()).save(group);
        verify(groupDao, times(1)).findByName("groupName");

        //cleanup
    }

    @Test
    public void update_isOk() throws Exception {
        //setup
        Group group = createMockGroup();
        when(groupDao.findByName("updatedGroupName")).thenReturn(Optional.empty());
        when(groupDao.findById(1)).thenReturn(Optional.of(group));
        doNothing().when(groupDao).update(group);

        //execute
        groupService.update("1", "updatedGroupName");
        assertEquals("updatedGroupName", group.getName());
        verify(groupDao, times(1)).findById(1);
        verify(groupDao, times(1)).findByName("updatedGroupName");
        verify(groupDao, times(1)).update(group);

        //cleanup
    }

    @Test
    public void update_nameEmpty_isOk() throws Exception {
        //setup
        Group group = createMockGroup();
        when(groupDao.findByName("updatedGroupName")).thenReturn(Optional.empty());
        when(groupDao.findById(1)).thenReturn(Optional.of(group));
        doNothing().when(groupDao).update(group);

        //execute
        groupService.update("1", null);
        assertEquals("groupName", group.getName());
        verify(groupDao, times(1)).findById(1);
        verify(groupDao, never()).findByName("updatedGroupName");
        verify(groupDao, times(1)).update(group);

        //cleanup
    }

    @Test
    public void update_duplicateName_throwsException() throws Exception {
        //setup
        Group group = createMockGroup();
        when(groupDao.findByName("updatedGroupName")).thenReturn(Optional.of(new Group()));
        when(groupDao.findById(1)).thenReturn(Optional.of(group));
        doNothing().when(groupDao).update(group);

        //execute
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                groupService.update("1", "updatedGroupName"));
        assertEquals("groupName", group.getName());
        assertEquals("Group name already taken!", exception.getMessage());
        verify(groupDao, times(1)).findById(1);
        verify(groupDao, times(1)).findByName("updatedGroupName");

        //cleanup
    }

    @Test
    public void delete_isOk() throws Exception {
        //setup
        Group group = createMockGroup();
        when(groupDao.findById(1)).thenReturn(Optional.of(group));
        doNothing().when(groupDao).delete(group);

        //execute
        assertDoesNotThrow(() -> groupService.delete("1"));
        verify(groupDao, times(1)).findById(1);
        verify(groupDao, times(1)).delete(group);

        //cleanup
    }

    @Test
    public void delete_incorrectId_throwsException() throws Exception {
        //setup
        when(groupDao.findById(1)).thenReturn(Optional.empty());

        //execute
        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> groupService.delete("1"));
        assertEquals("The given ID isn't associated with a group!", exception.getMessage());
        verify(groupDao, times(1)).findById(1);
        verify(groupDao, never()).delete(any());

        //cleanup
    }

}
