package me.mkweb.gibb.m183.securityapp.config

import me.mkweb.gibb.m183.securityapp.util.requestAddress
import me.mkweb.gibb.m183.securityapp.web.filter.RateLimitHolder
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
class SecurityConfig(val userDetailsService: UserDetailsService,
                     val rateLimitHolder: RateLimitHolder) : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun rateLimitingFilter(rateLimitHolder: RateLimitHolder): FilterRegistrationBean<RateLimitingFilter> {
        val registrationBean = FilterRegistrationBean<RateLimitingFilter>(RateLimitingFilter(rateLimitHolder))
        registrationBean.addUrlPatterns("/do-login/*")
        registrationBean.order = 5
        return registrationBean

    }

    override fun configure(web: WebSecurity?) {
        web!!.ignoring().antMatchers("/css/**", "/h2-console/**")
    }

    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
                .antMatchers("/css/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .usernameParameter("user").passwordParameter("pass")
                .loginProcessingUrl("/do-login")
                .successHandler { request, response, _ ->
                    rateLimitHolder.clear(request.requestAddress)
                    response.sendRedirect("/")
                }
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(userDetailsService)
    }
}