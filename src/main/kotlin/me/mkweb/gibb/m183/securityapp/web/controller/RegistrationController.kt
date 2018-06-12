package me.mkweb.gibb.m183.securityapp.web.controller

import me.mkweb.gibb.m183.securityapp.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RegistrationController(val userService: UserService) {
    @GetMapping("/register")
    fun register(model: Model): Any {
        return "register"
    }

    @PostMapping("/do-register")
    fun doRegister(model: Model,
                   @RequestParam username: String,
                   @RequestParam password: String,
                   @RequestParam password2: String): Any {
        model.addAttribute("message", userService.registerUser(username, password, password2))
        return "register"
    }
}