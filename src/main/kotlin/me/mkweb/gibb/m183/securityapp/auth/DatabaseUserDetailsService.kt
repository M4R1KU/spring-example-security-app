package me.mkweb.gibb.m183.securityapp.auth

import me.mkweb.gibb.m183.securityapp.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class DatabaseUserDetailsService(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username!!) ?: throw UsernameNotFoundException(username)
        return User(user.username, user.password, emptyList())
    }
}