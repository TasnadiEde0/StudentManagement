package org.learning.studentManagement.exception;

public class GroupNameDuplicateException extends RuntimeException {
    public GroupNameDuplicateException(String message) {
        super(message);
    }
    public GroupNameDuplicateException() {
        super("Group name already taken!");
    }
}
