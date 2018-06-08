package me.mkweb.gibb.m183.securityapp.domain

import me.mkweb.gibb.m183.securityapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class UserDataLoader(val passwordEncoder: PasswordEncoder,
                     val userRepository: UserRepository) {
    companion object {
        private const val USERNAME = "lb3"
        private const val PASSWORD = "gibbiX12345"
    }

    @PostConstruct
    fun init() {
        if (userRepository.findByUsername(USERNAME) == null) {
            val user = User(USERNAME, passwordEncoder.encode(PASSWORD))
            userRepository.save(user)
        }
    }
}