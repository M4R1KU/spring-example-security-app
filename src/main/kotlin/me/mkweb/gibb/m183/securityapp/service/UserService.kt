package me.mkweb.gibb.m183.securityapp.service

import me.mkweb.gibb.m183.securityapp.domain.User
import me.mkweb.gibb.m183.securityapp.repository.UserRepository
import me.mkweb.gibb.m183.securityapp.util.ViewResultType
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class UserService(val userRepository: UserRepository,
                  val passwordEncoder: PasswordEncoder) {
    companion object {
        private val PASSWORD_REGEX = Pattern.compile("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).{10,}")

        private val LOGGER = LoggerFactory.getLogger(UserService::class.java)
    }

    fun registerUser(username: String, password: String, password2: String): Pair<ViewResultType, String> {
        val matcher = PASSWORD_REGEX.matcher(password)
        if (!matcher.find()) {
            return ViewResultType.ERROR to "Password must be at least 10 characters long and contain upper and lowercase letters and numbers"
        }
        if (password != password2) {
            return ViewResultType.ERROR to "Passwords are not the same"
        }
        if (userRepository.findByUsername(username) != null) {
            return ViewResultType.ERROR to "Username already exists"
        }
        val user = userRepository.save(User(username, passwordEncoder.encode(password)))
        LOGGER.info("Created user with username: $username")
        return ViewResultType.SUCCESS to "Created user with name: ${user.username}"
    }

    fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }
}
