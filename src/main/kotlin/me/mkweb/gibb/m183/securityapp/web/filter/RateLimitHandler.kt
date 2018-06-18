package me.mkweb.gibb.m183.securityapp.web.filter

import me.mkweb.gibb.m183.securityapp.properties.RateLimitingProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RateLimitHandler(rateLimitingProperties: RateLimitingProperties) {
    private val timeFrame = minuteToMillis(rateLimitingProperties.timeFrameMinutes)
    private val blockedTime = minuteToMillis(rateLimitingProperties.blockedMinutes)
    private val requestsPerTimeFrame = rateLimitingProperties.requestsPerTimeFrame

    companion object {
        private val IP_ACCESS_MAP: MutableMap<String, MutableList<Long>> = mutableMapOf()
        private val BLOCKED_IPS: MutableMap<String, Long> = mutableMapOf()

        private val LOGGER = LoggerFactory.getLogger(RateLimitHandler::class.java)

        private fun minuteToMillis(minute: Long): Long {
            return minute * 60 * 1000
        }
    }

    fun checkLimitExceeded(ipAddress: String): Boolean {
        if (ipAddress.isEmpty()) {
            return false
        }
        if (checkBlocked(ipAddress)) {
            LOGGER.warn("Blocked IP $ipAddress attempted an access but was denied")
            return true
        }
        val now = System.currentTimeMillis()
        val lastAttempts = IP_ACCESS_MAP[ipAddress]
        // remove all entries that are not in the timeframe
        lastAttempts?.removeAll(isInTimeFrame(now))

        val hasExceeded = lastAttempts?.size ?: 0 >= requestsPerTimeFrame
        if (hasExceeded) {
            LOGGER.warn("Block IP $ipAddress for ${blockedTime / (60 * 1000)} minutes")
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

    fun checkBlocked(ipAddress: String): Boolean {
        val blockedTimestamp = BLOCKED_IPS[ipAddress] ?: return false
        val isBlocked = (System.currentTimeMillis() - blockedTime) < blockedTimestamp
        if (!isBlocked) {
            // remove IP from blocked map when blocked time passed
            BLOCKED_IPS.remove(ipAddress)
        }
        return isBlocked
    }

    fun clear(ipAddress: String) {
        LOGGER.info("Removing IP $ipAddress from access map")
        IP_ACCESS_MAP.remove(ipAddress)
    }

    private fun isInTimeFrame(now: Long): (time: Long) -> Boolean = { timestamp ->
        timestamp < (now - timeFrame)
    }
}