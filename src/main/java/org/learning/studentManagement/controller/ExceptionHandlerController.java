package org.learning.studentManagement.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(Model model, IllegalArgumentException ex) {
        model.addAttribute("errorMsg", ex.getMessage());

        return "error";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Image file not found")
    @ExceptionHandler(FileNotFoundException.class)
    public void handleFileNotFound(FileNotFoundException ex) {

    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(Model model, ConstraintViolationException ex) {
        String violations = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.joining(", "));
        model.addAttribute("errorMsg", violations);

        return "error";
    }

//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    public String handleException(Model model, Exception ex) {
//        model.addAttribute("errorMsg", ex.getMessage());
//
//        log.error(ex.getMessage(), ex);
//
//        return "error";
//    }

}
