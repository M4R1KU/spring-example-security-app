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

class RateLimitingFilter : GenericFilterBean() {
    companion object {
        private val IP_ACCESS_MAP = HashMap<String, LocalDateTime>()
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val remoteAddr = request.remoteAddr
        val lastAccess = IP_ACCESS_MAP[remoteAddr]
        if (lastAccess != null && !lastAccess.isBefore(LocalDateTime.now().minus(500, ChronoUnit.MICROS))) {
            throw RateLimitExceededException("Remote with address $remoteAddr has exceeded its rate limit of 1 request per hour")
        }
        IP_ACCESS_MAP[remoteAddr] = LocalDateTime.now()

        chain.doFilter(request, response)
    }
}
