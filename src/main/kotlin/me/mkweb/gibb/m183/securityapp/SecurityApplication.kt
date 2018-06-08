package me.mkweb.gibb.m183.securityapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SecurityAppApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SecurityAppApplication::class.java, *args)
        }
    }
}