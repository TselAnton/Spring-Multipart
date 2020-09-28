package com.tsel.multipart.controller;

import static com.tsel.multipart.exception.MyWebException.throwEx;

import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingController {

    @GetMapping("/")
    public void greetingResponse(HttpServletResponse response) {
        try {
            response.sendRedirect("/files");
        } catch (Exception e) {
            throw throwEx(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong while redirect to /file path", e);
        }
    }
}
