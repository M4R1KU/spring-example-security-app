package me.mkweb.gibb.m183.securityapp.web.controller

import me.mkweb.gibb.m183.securityapp.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView

@Controller
class RegistrationController(val userService: UserService) {
    @ModelAttribute("users")
    fun users() = userService.findAllUsers()

    @GetMapping("/register")
    fun register(): Any {
        return "register"
    }

    @PostMapping("/register")
    fun doRegister(redirectAttributes: RedirectAttributes,
                   @RequestParam username: String,
                   @RequestParam password: String,
                   @RequestParam password2: String): Any {
        redirectAttributes.addFlashAttribute("message", userService.registerUser(username, password, password2))
        return RedirectView("register")
    }
}