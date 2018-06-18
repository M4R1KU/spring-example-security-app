package me.mkweb.gibb.m183.securityapp.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "securityapp.rate-limiting")
class RateLimitingProperties {
    var timeFrameMinutes: Long = 5L
    var blockedMinutes: Long = 60L
    var requestsPerTimeFrame: Long = 10L
}