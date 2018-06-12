package me.mkweb.gibb.m183.securityapp.web.controller

import me.mkweb.gibb.m183.securityapp.service.SystemCommandService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/command")
class CommandController(val systemCommandService: SystemCommandService) {
    @GetMapping
    fun command(model: Model): Any {
        return "command"
    }

    @GetMapping("/execute")
    fun executeCommand( model: Model, @RequestParam("sysopt") sysopt: String): Any {
        model.addAttribute("result", systemCommandService.executeSecuredCommand("date", sysopt))
        return "command"
     }
}