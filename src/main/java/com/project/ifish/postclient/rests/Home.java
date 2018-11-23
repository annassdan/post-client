package com.project.ifish.postclient.rests;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressWarnings("unused")
public class Home {

    @GetMapping("/")
    public String home() {
        return "home";
    }

}
