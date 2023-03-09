package de.bagra.carwebcrawler.service

import de.bagra.carwebcrawler.runnable.CrawlerStatistics
import de.bagra.carwebcrawler.runnable.CrawlerTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture


@Service
class CrawlerService(val crawlerStatistics: CrawlerStatistics, val taskScheduler: ThreadPoolTaskScheduler) {


    private lateinit var taskState: ListenableFuture<*>

    fun startCrawling() {
        taskState = taskScheduler.submitListenable(CrawlerTaskExecutor(crawlerStatistics))
    }

    fun stopForceCrawling() {
        if (taskState != null && !taskState!!.isDone) {
            crawlerStatistics.interrupt = true
        }
    }

    fun getResult(): String {
        return "Crawl-runs ${crawlerStatistics.runs} - total articles ${crawlerStatistics.crawledArticles.size} - total new article ${crawlerStatistics.crawledArtilceNumber}"
    }
}