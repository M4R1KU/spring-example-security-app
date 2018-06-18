package me.mkweb.gibb.m183.securityapp.util

enum class ViewResultType {
    SUCCESS,
    NEUTRAL,
    ERROR;

    val clazz get() = name.toLowerCase()
}