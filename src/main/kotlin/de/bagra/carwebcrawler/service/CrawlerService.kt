package de.bagra.carwebcrawler.service

import de.bagra.carwebcrawler.repository.CrawlerDataRepository
import de.bagra.carwebcrawler.runnable.CrawlerStatistics
import de.bagra.carwebcrawler.runnable.CrawlerTaskExecutor
import de.bagra.carwebcrawler.util.MapperUtil
import de.bagra.carwebcrawler.vaadin.model.CrawledData
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors


@Service
class CrawlerService(val crawlerStatistics: CrawlerStatistics, val taskScheduler: ThreadPoolTaskScheduler, val crawlerDataRepository: CrawlerDataRepository) {


    private lateinit var taskState: CompletableFuture<*>

    fun startCrawling() {
        taskState = taskScheduler.submitCompletable(CrawlerTaskExecutor(crawlerStatistics, crawlerDataRepository))
    }

    fun stopForceCrawling() {
        if (taskState != null && !taskState!!.isDone) {
            crawlerStatistics.interrupt = true
            taskState.completeExceptionally(throw CancellationException("interrupted manually"))
        }
    }

    fun getCrawledData(): MutableList<CrawledData>? {
        return crawlerDataRepository.findAll().stream().map(MapperUtil::mapToDto).collect(Collectors.toList())
    }

    fun getResult(): String {
        return "Crawl-runs ${crawlerStatistics.runs} - total articles ${crawlerStatistics.crawledArticles.size} - total new article ${crawlerStatistics.crawledArtilceNumber}"
    }
}