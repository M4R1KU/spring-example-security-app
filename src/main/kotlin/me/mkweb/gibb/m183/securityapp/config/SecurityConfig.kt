package me.mkweb.gibb.m183.securityapp.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.web.filter.CommonsRequestLoggingFilter

@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {
    companion object {
        @Bean
        fun passwordEncoder() = NoOpPasswordEncoder.getInstance()
    }

    override fun configure(http: HttpSecurity?) {
        http!!.formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth_error")
                .and()
                .logout()
                .logoutUrl("/do-logout").permitAll()
                .logoutSuccessUrl("/")
                .and()
                .authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .anyRequest().hasRole("USER")
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.inMemoryAuthentication().withUser("lb3").password("gibbiX12345").roles("USER")
    }
}