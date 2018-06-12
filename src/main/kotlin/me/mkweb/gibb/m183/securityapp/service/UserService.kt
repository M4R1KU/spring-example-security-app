package me.mkweb.gibb.m183.securityapp.service

import me.mkweb.gibb.m183.securityapp.domain.User
import me.mkweb.gibb.m183.securityapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository,
                  val passwordEncoder: PasswordEncoder) {
    fun registerUser(username: String, password: String, password2: String): String {
        if (!(password == password2 && userRepository.findByUsername(username) == null)) {
            return "Error: Username already exists or the passwords do not match"
        }
        userRepository.save(User(username, passwordEncoder.encode(password)))
        return "Success: Created user with name: $username"
    }
}
