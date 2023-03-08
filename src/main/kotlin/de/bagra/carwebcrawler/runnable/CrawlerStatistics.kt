package de.bagra.carwebcrawler.runnable

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CrawlerStatistics {
    lateinit var startDateTime: LocalDateTime
    var crawledSites: Int = 0
    var runs: Int = 0
    var crawledArticles: MutableMap<String, String> = mutableMapOf()
    var done = false
    var lastResponseStatusCode = 0
    var crawledArtilceNumber = 0
}