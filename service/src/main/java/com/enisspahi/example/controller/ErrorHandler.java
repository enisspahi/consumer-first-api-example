package com.enisspahi.example.controller;

import com.enisspahi.example.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(RecipeNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error notFound() {
        return new Error(Error.ErrorCode.NOT_FOUND, "Not found");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error unexpected() {
        return new Error(Error.ErrorCode.UNEXPECTED, "Unexpected error");
    }

}
