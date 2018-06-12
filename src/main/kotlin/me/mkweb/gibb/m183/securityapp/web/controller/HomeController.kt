package me.mkweb.gibb.m183.securityapp.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping
class HomeController {
    @GetMapping
    fun home(): Any {
        return RedirectView("command")
    }
}