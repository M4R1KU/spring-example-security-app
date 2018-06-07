package me.mkweb.gibb.m183.securityapp.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.JdbcUserDetailsManager
import javax.sql.DataSource

@EnableWebSecurity
class SecurityConfig(val dataSource: DataSource) : WebSecurityConfigurerAdapter() {
    override fun configure(auth: AuthenticationManagerBuilder?) {
        val builder = User.builder()
        auth!!.jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
    }

    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
                .antMatchers("/h2-console/**", "/css/**", "/login").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .headers().frameOptions().sameOrigin()

                .and().formLogin().loginPage("/login")
    }
}