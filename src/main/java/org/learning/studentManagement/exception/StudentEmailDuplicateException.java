package org.learning.studentManagement.exception;

public class StudentEmailDuplicateException extends RuntimeException {
    public StudentEmailDuplicateException(String message) {
        super(message);
    }
    public StudentEmailDuplicateException() {
        super("Student Email already taken!");
    }
}

