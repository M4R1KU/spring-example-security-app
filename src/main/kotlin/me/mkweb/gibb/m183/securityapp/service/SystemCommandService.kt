package me.mkweb.gibb.m183.securityapp.service

import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SystemCommandService {
    companion object {
        val LOGGER = LoggerFactory.getLogger(SystemCommandService::class.java)

        fun convertAndValidateParameters(arguments: String): Array<String> {
            return arguments.split(" ").map { StringEscapeUtils.ESCAPE_XSI.translate(it) }.toTypedArray()
        }
    }

    fun executeSecuredCommand(command: String, arguments: String): String {
        val argumentArray = convertAndValidateParameters(arguments)
        val processBuilder = ProcessBuilder(command, *argumentArray)
        LOGGER.info("Executing system command: ${processBuilder.command()} with parameters: ${argumentArray.joinToString(" ")}")
        val process = processBuilder.start()
        val errorOutput = process.errorStream.bufferedReader().use { it.readText() }
        if (errorOutput.isNotBlank()) {
            LOGGER.warn(errorOutput)
        }
        return process.inputStream.bufferedReader().use { it.readText() }
    }
}