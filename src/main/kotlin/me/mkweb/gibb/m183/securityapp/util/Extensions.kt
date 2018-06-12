package me.mkweb.gibb.m183.securityapp.util

import javax.servlet.http.HttpServletRequest

val HttpServletRequest.requestAddress: String
    get() {
        val forwardedHeader = getHeader("X-FORWARDED-FOR")
        if (forwardedHeader.isNullOrEmpty()){
            return remoteAddr
        }
        return forwardedHeader
    }