package me.mkweb.gibb.m183.securityapp.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/login")
class LoginController {
    @GetMapping
    fun login(model: Model): Any {
        return "login"
    }
}
