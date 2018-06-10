package me.mkweb.gibb.m183.securityapp.web.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalControllerAdvice {
    @ModelAttribute("currentUser")
    fun currentUser(): Any? {
        val principal = SecurityContextHolder.getContext().authentication.principal
        if (principal is String && principal == "anonymousUser") {
            return null
        }
        return principal
    }
}