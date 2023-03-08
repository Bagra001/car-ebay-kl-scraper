package de.bagra.carwebcrawler.service

import de.bagra.carwebcrawler.runnable.CrawlerStatistics
import de.bagra.carwebcrawler.runnable.CrawlerTaskExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledFuture





@Service
class CrawlerService {

    @Autowired
    private lateinit var taskScheduler: TaskScheduler

    @Autowired
    private lateinit var crawlerStatistics: CrawlerStatistics

    private var taskState: ScheduledFuture<*>? = null
    
    fun startCrawling() {
        taskState = taskScheduler.schedule(CrawlerTaskExecutor(crawlerStatistics), Instant.now())
    }

    fun stopCrawling(): Boolean {
        var stopped: Boolean = false;
        if (taskState != null) {
            taskState?.cancel(false)
            stopped = true
        }
        return stopped
    }

    fun stopForceCrawling(): Boolean {
        var stopped: Boolean = false;
        if (taskState != null) {
            taskState?.cancel(true)
            stopped = true
        }
        return stopped
    }
}