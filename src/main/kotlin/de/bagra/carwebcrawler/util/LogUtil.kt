package de.bagra.carwebcrawler.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LogUtil {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun logError(msg: String, exception: Throwable) {
        log.error(msg, exception.stackTrace)
    }
}