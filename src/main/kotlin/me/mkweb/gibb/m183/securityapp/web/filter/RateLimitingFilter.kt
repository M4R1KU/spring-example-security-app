package me.mkweb.gibb.m183.securityapp.web.filter

import me.mkweb.gibb.m183.securityapp.util.requestAddress
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RateLimitingFilter(private val rateLimitHandler: RateLimitHandler) : GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            val remoteAddress = request.requestAddress
            if (rateLimitHandler.checkLimitExceeded(remoteAddress)) {
                response.sendRedirect("/login?error")
                return
            }
        }
        chain.doFilter(request, response)
    }
}
