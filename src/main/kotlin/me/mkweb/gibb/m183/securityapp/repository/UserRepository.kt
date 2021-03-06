package me.mkweb.gibb.m183.securityapp.repository

import me.mkweb.gibb.m183.securityapp.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    fun findByUsername(username: String): User?
}