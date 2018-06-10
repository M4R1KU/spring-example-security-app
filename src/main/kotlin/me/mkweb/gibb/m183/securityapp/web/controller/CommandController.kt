package me.mkweb.gibb.m183.securityapp.web.controller

import me.mkweb.gibb.m183.securityapp.service.SystemCommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/command")
class CommandController @Autowired constructor(val systemCommandService: SystemCommandService) {
    @GetMapping()
    fun command(model: Model): Any {
        return "command"
    }

    @GetMapping("/execute")
    fun executeCommand( model: Model, @RequestParam("sysopt") sysopt: String): Any {
        model.addAttribute("result", systemCommandService.executeSecuredCommand("date", sysopt))
        return "command"
    }
}