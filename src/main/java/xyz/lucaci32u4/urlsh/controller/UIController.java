package xyz.lucaci32u4.urlsh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.lucaci32u4.urlsh.Config;
import xyz.lucaci32u4.urlsh.data.UrlRepository;
import xyz.lucaci32u4.urlsh.data.stats.StatsRepository;

@Controller
public class UIController {

    @Autowired
    private Config config;

    @Autowired
    private UrlRepository urlRepository;

    @GetMapping("/favicon.ico")
    public String redirectFavicon() {
        return "redirect:/-/favicon.ico";
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("config", config);
        return "index";
    }

    @GetMapping("/-/stats/{reference}")
    public String getStats(@PathVariable("reference") String reference, Model model) {

        var url = urlRepository.findById(reference).orElse(null);

        model.addAttribute("config", config);
        model.addAttribute("url", url);
        return "stats";
    }



}
