package org.learning.studentManagement.exception;

public class StudentCnpDuplicateException extends RuntimeException {
    public StudentCnpDuplicateException(String message) {
        super(message);
    }
    public StudentCnpDuplicateException() {
        super("Student CNP already taken!");
    }
}
