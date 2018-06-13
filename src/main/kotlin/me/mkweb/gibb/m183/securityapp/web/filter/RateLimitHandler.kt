package me.mkweb.gibb.m183.securityapp.web.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RateLimitHandler {
    companion object {
        const val TIMEFRAME_MINUTES = 5L
        const val REQUESTS_PER_TIMEFRAME = 10
        const val BLOCKED_HOURS = 1L

        private val IP_ACCESS_MAP: MutableMap<String, MutableList<LocalDateTime>> = mutableMapOf()
        private val BLOCKED_IPS: MutableMap<String, LocalDateTime> = mutableMapOf()

        private val LOGGER = LoggerFactory.getLogger(RateLimitHandler::class.java)
    }

    fun checkLimitExceeded(ipAddress: String): Boolean {
        if (ipAddress.isEmpty()) {
            return false
        }
        if (isBlocked(ipAddress)) {
            LOGGER.warn("Blocked IP $ipAddress attempted an access")
            return true
        }
        val now = LocalDateTime.now()
        val lastAttempts = IP_ACCESS_MAP[ipAddress]
        lastAttempts?.removeAll(isInTimeFrame(now))

        val hasExceeded = lastAttempts?.size ?: 0 >= REQUESTS_PER_TIMEFRAME
        if (hasExceeded) {
            LOGGER.warn("Block IP $ipAddress for $BLOCKED_HOURS hours")
            IP_ACCESS_MAP.remove(ipAddress)
            BLOCKED_IPS[ipAddress] = now
        } else {
            IP_ACCESS_MAP.compute(ipAddress) { _, attempts ->
                val attemptsOrEmpty = attempts ?: mutableListOf()
                attemptsOrEmpty.add(now)
                attemptsOrEmpty
            }
        }
        return hasExceeded
    }

    fun clear(ipAddress: String) {
        LOGGER.info("Removing IP $ipAddress from access map")
        IP_ACCESS_MAP.remove(ipAddress)
    }

    private fun isInTimeFrame(now: LocalDateTime): (time: LocalDateTime) -> Boolean {
        return { time ->
            time.isBefore(now.minusMinutes(TIMEFRAME_MINUTES))
        }
    }

    private fun isBlocked(ipAddress: String): Boolean {
        return BLOCKED_IPS[ipAddress]?.isAfter(LocalDateTime.now().minusDays(BLOCKED_HOURS)) ?: false
    }
}