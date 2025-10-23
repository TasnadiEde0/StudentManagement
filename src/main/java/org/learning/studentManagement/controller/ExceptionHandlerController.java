package org.learning.studentManagement.controller;

import org.learning.studentManagement.exception.StudentCnpDuplicateException;
import org.learning.studentManagement.exception.GroupNameDuplicateException;
import org.learning.studentManagement.exception.StudentEmailDuplicateException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentCnpDuplicateException.class)
    public String handleCnpDuplicateException(Model model, StudentCnpDuplicateException ex) {
        model.addAttribute("errorMsg", ex.getMessage());

        return "error";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentEmailDuplicateException.class)
    public String handleEmailDuplicateException(Model model, StudentEmailDuplicateException ex) {
        model.addAttribute("errorMsg", ex.getMessage());

        return "error";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GroupNameDuplicateException.class)
    public String handleNameDuplicateException(Model model, GroupNameDuplicateException ex) {
        model.addAttribute("errorMsg", ex.getMessage());

        return "error";
    }

}
