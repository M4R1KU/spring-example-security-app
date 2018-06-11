package me.mkweb.gibb.m183.securityapp.config

import me.mkweb.gibb.m183.securityapp.web.filter.RateLimitingFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun rateLimitingFilter(): FilterRegistrationBean<RateLimitingFilter> {
        val registrationBean = FilterRegistrationBean<RateLimitingFilter>(RateLimitingFilter())
        registrationBean.addUrlPatterns("/do-login/*")
        registrationBean.order = 5
        return registrationBean

    }

    override fun configure(web: WebSecurity?) {
        web!!.ignoring().antMatchers("/css/**")
    }

    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/command")
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(userDetailsService)
    }
}