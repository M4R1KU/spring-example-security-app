package me.mkweb.gibb.m183.securityapp.web.filter

import me.mkweb.gibb.m183.securityapp.web.filter.exception.RateLimitExceededException
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class RateLimitingFilter : GenericFilterBean() {
    companion object {
        private val IP_ACCESS_MAP = HashMap<String, LocalDateTime>()
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            var remoteAddr = request.getHeader("X-FORWARDED_FOR")
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.remoteAddr
            }
            val lastAccess = IP_ACCESS_MAP[remoteAddr]
            if (lastAccess != null && !lastAccess.isBefore(LocalDateTime.now().minus(500, ChronoUnit.MILLIS))) {
                throw RateLimitExceededException("Remote with address $remoteAddr has exceeded its rate limit of 1 request per 500 milliseconds")
            }
            IP_ACCESS_MAP[remoteAddr] = LocalDateTime.now()

        }
        chain.doFilter(request, response)
    }
}
