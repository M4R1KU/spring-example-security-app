package me.mkweb.gibb.m183.securityapp.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll()
                .and().csrf().disable()
                .headers().frameOptions().sameOrigin()
    }
}