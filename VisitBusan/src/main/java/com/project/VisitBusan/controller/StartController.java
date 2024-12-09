package com.project.VisitBusan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class StartController {

    @GetMapping("/")
    public String mainpage(Model model) {
        return "main"; // http://localhost:9089/
    }

}
