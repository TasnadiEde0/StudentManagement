package org.learning.studentManagement.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    /**
     * Handles incorrect IDs, duplicate names
     * Catches IllegalArgumentExceptions
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(Model model, IllegalArgumentException ex) {
        if(ex instanceof NumberFormatException) {
            model.addAttribute("errorMsg", "Not a vaild ID: " + ex.getMessage().substring(17));
        }
        else {
            model.addAttribute("errorMsg", ex.getMessage());
        }

        return "error";
    }

    /**
     * Handles violating input constraints
     * Catches ConstraintViolationException
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(Model model, ConstraintViolationException ex) {
        String violations = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.joining(", "));
        model.addAttribute("errorMsg", violations);

        return "error";
    }

    /**
     * Handles Student profile picture not found
     * Catches FileNotFoundException
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Image file not found")
    @ExceptionHandler(FileNotFoundException.class)
    public void handleFileNotFound(FileNotFoundException ex) {

    }

}
