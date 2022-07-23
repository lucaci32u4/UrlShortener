package xyz.lucaci32u4.urlsh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/favicon.ico")
    public String redirectFavicon() {
        return "redirect:/-/favicon.ico";
    }

}
