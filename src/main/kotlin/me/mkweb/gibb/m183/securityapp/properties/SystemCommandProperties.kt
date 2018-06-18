package me.mkweb.gibb.m183.securityapp.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "securityapp.system-command")
class SystemCommandProperties {
    var commandName: String = "ls"
}