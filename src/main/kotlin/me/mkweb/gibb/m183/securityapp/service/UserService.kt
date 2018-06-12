package me.mkweb.gibb.m183.securityapp.service

import me.mkweb.gibb.m183.securityapp.domain.User
import me.mkweb.gibb.m183.securityapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class UserService(val userRepository: UserRepository,
                  val passwordEncoder: PasswordEncoder) {
    companion object {
        private val PASSWORD_REGEX = Pattern.compile("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).{10,}");
    }

    fun registerUser(username: String, password: String, password2: String): String {
        val matcher = PASSWORD_REGEX.matcher(password)
        if (!matcher.find()) {
            return "Error: Password must be at least 10 characters long and contain upper and lowercase letters and numbers"
        }

        if (!(password == password2 && userRepository.findByUsername(username) == null)) {
            return "Error: Username already exists or the passwords do not match"
        }
        userRepository.save(User(username, passwordEncoder.encode(password)))
        return "Success: Created user with name: $username"
    }

    fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }
}
