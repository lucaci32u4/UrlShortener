package xyz.lucaci32u4.urlsh.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import xyz.lucaci32u4.urlsh.Utils;
import xyz.lucaci32u4.urlsh.data.UrlEntry;
import xyz.lucaci32u4.urlsh.data.UrlRepository;
import xyz.lucaci32u4.urlsh.data.stats.StatsManager;
import xyz.lucaci32u4.urlsh.data.stats.StatsRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;

@Controller
@Slf4j
public class UrlController {

    private final UrlRepository urlRepository;
    private final StatsManager statsManager;

    @Autowired
    public UrlController(UrlRepository urlRepository, StatsManager statsManager) {
        this.urlRepository = urlRepository;
        this.statsManager = statsManager;
    }

    @PostMapping("/-/create")
    @ResponseBody
    public ResponseEntity<Record> createShorten(@Valid @RequestBody CreateRequest request, @RequestHeader(name = "x-forwarded-for", required = false) String proxyFor, HttpServletRequest servreq) {
        var srcIp = Utils.unpackSourceIpProxy(proxyFor, servreq.getRemoteAddr());
        UrlEntry entry = new UrlEntry();
        entry.setCreatorIp(srcIp);
        entry.setUrl(request.url());
        entry.setCreatorTs(Instant.now());
        entry.setInpageRedirect(request.inpageRedirect());
        try {
            var url = new URL(entry.getUrl());
            if (!url.getProtocol().equals("https") && !url.getProtocol().equals("http")) {
                return new ResponseEntity<>(new DataError("Only HTTP and HTTPS protocols are supported"), HttpStatus.BAD_REQUEST);
            }
        } catch (MalformedURLException e) {
            entry.setUrl("https://" + entry.getUrl());
            try {
                var url = new URL(entry.getUrl());
            } catch (MalformedURLException ee) {
                return new ResponseEntity<>(new DataError("URL is malformed"), HttpStatus.BAD_REQUEST);
            }
        }
        if (request.alias() == null) {
            entry.setReference(statsManager.allocateNewReference());
        } else {
            var ref = request.alias().trim();
            if (urlRepository.findById(ref).isPresent()) {
                return new ResponseEntity<>(new DataError("Custom shortened URL already exists"), HttpStatus.BAD_REQUEST);
            }
            entry.setReference(request.alias());
        }
        urlRepository.save(entry);
        return new ResponseEntity<>(new CreateResponse(entry.getReference()), HttpStatus.OK);
    }

    @GetMapping("/{reference}")
    public String redirect(@PathVariable("reference") String ref, Model model) {
        var url = urlRepository.findById(ref).orElse(null);
        if (url == null) {
            return "redirect:/";
        }

        if (url.isInpageRedirect()) {
            model.addAttribute("location", url.getUrl());
            return "inpage";
        } else {
            return "redirect:" + url.getUrl();
        }
    }


}
