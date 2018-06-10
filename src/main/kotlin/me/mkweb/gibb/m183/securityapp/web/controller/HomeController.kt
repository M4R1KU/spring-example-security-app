package me.mkweb.gibb.m183.securityapp.web.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class HomeController {
    @GetMapping
    fun home(model: Model): Any {
        return "home"
    }
}