package me.mkweb.gibb.m183.securityapp.config

import me.mkweb.gibb.m183.securityapp.web.filter.RateLimitingFilter
import me.mkweb.gibb.m183.securityapp.web.filter.exception.RateLimitExceededException
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity?) {
        web!!.debug(true)
    }

    override fun configure(http: HttpSecurity?) {
        http!!.antMatcher("/do-login")
                .addFilterAfter(RateLimitingFilter(), BasicAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/h2-console/**", "/css/**", "/login").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .headers().frameOptions().sameOrigin()

                .and().formLogin().loginPage("/login")
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/command")
                .and()
                .logout().permitAll()

    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(userDetailsService)
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}