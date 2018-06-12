package me.mkweb.gibb.m183.securityapp.web.filter

import me.mkweb.gibb.m183.securityapp.util.requestAddress
import org.slf4j.LoggerFactory
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RateLimitingFilter(val rateLimitHolder: RateLimitHolder) : GenericFilterBean() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RateLimitingFilter::class.java)
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            var remoteAddr = request.requestAddress
            if (rateLimitHolder.checkLimitExceeded(remoteAddr)) {
                response.sendRedirect("/login?error")
                return
            }
        }
        chain.doFilter(request, response)
    }
}
