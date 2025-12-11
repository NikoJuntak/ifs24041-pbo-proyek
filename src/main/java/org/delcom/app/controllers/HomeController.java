package org.delcom.app.controllers;

import org.delcom.app.utils.ConstUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landingPage() {
        // Mengembalikan view index.html
        return ConstUtil.TEMPLATE_INDEX;
    }
}