package de.bagra.carwebcrawler.util

import de.bagra.carwebcrawler.runnable.CrawlerStatistics
import org.jsoup.nodes.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Component
class LogUtil {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)

        fun loadLog(crawlerStatistics: CrawlerStatistics) {
            if (crawlerStatistics.crawledArticles.isEmpty()) {
                val path: Path = Path.of("crawledArticles.txt")
                if (Files.exists(path)) {
                    val allLines: List<String> = Files.readAllLines(path)
                    for (line: String in allLines) {
                        val splittedLine: List<String> = line.split(",")
                        crawlerStatistics.crawledArticles[splittedLine[0]] = splittedLine[1]
                    }
                    log.info("log-file loaded")
                }
            }
        }

        fun writeLog(article: Element) {
            val path: Path = Path.of("crawledArticles.txt")
            if(path != null && Files.notExists(path)) {
                Files.createFile(path);
            }
            val stringBuilder: StringBuilder = java.lang.StringBuilder()
            stringBuilder.append(article.attr("data-adid"))
            stringBuilder.append(",")
            stringBuilder.append(article.attr("data-href"))
            stringBuilder.append("\n")
            Files.writeString(path, stringBuilder.toString(), StandardOpenOption.APPEND)
        }
    }

    fun logError(msg: String, exception: Throwable) {
        log.error(msg, exception.stackTrace)
    }
}