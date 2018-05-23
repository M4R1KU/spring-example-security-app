package me.mkweb.gibb.m183.securityapp

import me.mkweb.gibb.m183.securityapp.domain.User
import me.mkweb.gibb.m183.securityapp.repository.UserRepository
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import javax.annotation.PostConstruct

@SpringBootApplication
class SecurityAppApplication(val userRepository: UserRepository) {

    @PostConstruct
    fun load() {
        userRepository.save(User("lb3", "gibbiX12345"))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SecurityAppApplication::class.java, *args)
        }
    }
}