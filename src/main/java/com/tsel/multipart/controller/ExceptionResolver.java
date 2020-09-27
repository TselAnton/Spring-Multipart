package com.tsel.multipart.controller;

import com.tsel.multipart.data.ResponseErrorMessage;
import com.tsel.multipart.exception.MyWebException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MyWebException.class)
    public ResponseEntity<ResponseErrorMessage> handleMyWebException(MyWebException e) {
        return ResponseEntity
                .status(e.getErrorStatus())
                .body(new ResponseErrorMessage(e.getErrorStatus(), e.getMessage(), e.getCause()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseErrorMessage> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ResponseErrorMessage(
                        HttpStatus.PAYLOAD_TOO_LARGE, "The uploaded file is too large!", e));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseErrorMessage> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(new ResponseErrorMessage(
                        HttpStatus.I_AM_A_TEAPOT, "Something went wrong", e));
    }
}
