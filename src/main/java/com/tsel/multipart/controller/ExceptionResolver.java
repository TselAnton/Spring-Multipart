package com.tsel.multipart.controller;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import com.tsel.multipart.data.ResponseErrorMessage;
import com.tsel.multipart.exception.MyWebException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

    private static final List<HttpStatus> WARN_STATUSES = asList(UNSUPPORTED_MEDIA_TYPE, BAD_REQUEST, NOT_FOUND);

    @ExceptionHandler(MyWebException.class)
    public ResponseEntity<ResponseErrorMessage> handleMyWebException(MyWebException e) {
        logException(e);
        return ResponseEntity
                .status(e.getErrorStatus())
                .body(new ResponseErrorMessage(e.getErrorStatus(), e.getMessage(), e.getCause()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseErrorMessage> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.warn("File too large!", e);
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ResponseErrorMessage(
                        HttpStatus.PAYLOAD_TOO_LARGE, "The uploaded file is too large!", e));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseErrorMessage> handleException(Exception e) {
        logger.error("Something went wrong!", e);
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(new ResponseErrorMessage(
                        HttpStatus.I_AM_A_TEAPOT, "Something went wrong", e));
    }

    private void logException(MyWebException e) {
        if (WARN_STATUSES.contains(e.getErrorStatus())) {
            logger.warn(e.getMessage());
        } else {
            logger.error(e.getMessage(), e.getCause());
        }
    }
}
