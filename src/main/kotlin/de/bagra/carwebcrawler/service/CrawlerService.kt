package de.bagra.carwebcrawler.service

import de.bagra.carwebcrawler.runnable.CrawlerStatistics
import de.bagra.carwebcrawler.runnable.CrawlerTaskExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ScheduledFuture


@Service
class CrawlerService {

    @Autowired
    private lateinit var taskScheduler: ThreadPoolTaskScheduler

    @Autowired
    private lateinit var crawlerStatistics: CrawlerStatistics

    private var taskState: ScheduledFuture<*>? = null
    
    fun startCrawling(): ScheduledFuture<*>? {
        taskState = taskScheduler.schedule(CrawlerTaskExecutor(crawlerStatistics), Instant.now())
        return taskState
    }

    fun stopCrawling(): Boolean {
        var stopped = false
        if (taskState != null && !taskState!!.isDone) {
            taskState?.cancel(false)
            stopped = true
        }
        return stopped
    }

    fun stopForceCrawling(): Boolean {
        var stopped = false
        if (taskState != null && !taskState!!.isDone) {
            taskState?.cancel(true)
            stopped = true
        }
        return stopped
    }
}