package me.mkweb.gibb.m183.securityapp.service

import me.mkweb.gibb.m183.securityapp.util.ViewResultType
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import kotlin.streams.toList

@Service
class SystemCommandService {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SystemCommandService::class.java)

        fun convertAndValidateParameters(arguments: String): Array<String> {
            return arguments.split(" ").map { StringEscapeUtils.ESCAPE_XSI.translate(it) }.toTypedArray()
        }
    }

    fun executeSecuredCommand(command: String, arguments: String): Pair<ViewResultType, Array<String>> {
        val argumentArray = convertAndValidateParameters(arguments)
        val processBuilder = ProcessBuilder(command, *argumentArray)

        LOGGER.info("Executing system command: {} with arguments: {}", command, arguments)
        return try {
            val process = processBuilder.start()

            val errors = process.errorStream.bufferedReader().lines().toList().toTypedArray()
            if (errors.isNotEmpty()) {
                LOGGER.trace("Command {} with arguments {} has errored: {}", command, arguments, errors)
                return ViewResultType.ERROR to errors
            }
            val output = process.inputStream.bufferedReader().lines().toList().toTypedArray()
            LOGGER.trace("Command {} with arguments {} returned: {}", command, arguments, output)
            ViewResultType.NEUTRAL to output
        } catch (ioex: IOException) {
            LOGGER.error("Failed to execute command $command with arguments $arguments", ioex)
            ViewResultType.ERROR to arrayOf(ioex.message ?: "")
        }
    }
}