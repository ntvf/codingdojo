package me._on.codingdojo.server.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
        "/",
        "/{path:[a-zA-Z0-9\\-]+}",
        "/{path:[a-zA-Z0-9\\-]+}/**"
    })
    public String index() {
        return "forward:/index.html";
    }
}



