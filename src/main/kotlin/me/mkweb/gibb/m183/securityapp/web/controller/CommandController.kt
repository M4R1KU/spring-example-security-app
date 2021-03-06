package me.mkweb.gibb.m183.securityapp.web.controller

import me.mkweb.gibb.m183.securityapp.properties.SystemCommandProperties
import me.mkweb.gibb.m183.securityapp.service.SystemCommandService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/command")
class CommandController(val systemCommandService: SystemCommandService,
                        val systemCommandProperties: SystemCommandProperties) {
    @ModelAttribute("commandName")
    fun command() = systemCommandProperties.commandName

    @GetMapping
    fun command(model: Model): Any {
        return "command"
    }

    @GetMapping("/execute")
    fun executeCommand(model: Model, @RequestParam("sysopt") arguments: String): Any {
        val systemCommandResult = systemCommandService.executeSecuredCommand(systemCommandProperties.commandName, arguments)
        model.addAttribute("arguments", arguments)
                .addAttribute("type", systemCommandResult.first.clazz)
                .addAttribute("result", systemCommandResult.second)
        return "command"
    }
}