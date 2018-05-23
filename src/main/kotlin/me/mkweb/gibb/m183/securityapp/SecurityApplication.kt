package me.mkweb.gibb.m183.securityapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SecurityAppApplication

fun main(args: Array<String>) {
    SpringApplication.run(SecurityAppApplication::class.java, *args)
}