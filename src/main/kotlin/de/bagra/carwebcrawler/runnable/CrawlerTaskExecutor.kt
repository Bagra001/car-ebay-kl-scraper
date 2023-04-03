package de.bagra.carwebcrawler.runnable

import de.bagra.carwebcrawler.repository.CrawlerDataRepository
import org.jsoup.Connection.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime

class CrawlerTaskExecutor(val crawlerStatistics: CrawlerStatistics, val crawlerDataRepository: CrawlerDataRepository): Runnable {

    
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
    
    override fun run() {
        crawlerStatistics.done = false
        crawlerStatistics.interrupt = false
        crawlerStatistics.startDateTime = LocalDateTime.now()
        loadLog()
        for (url: String in crawlerStatistics.urlsToCrawl.keys) {
            crawlerStatistics.runs++
            while (!crawlerStatistics.done && !crawlerStatistics.interrupt) {
                crawl(url)
            }
        }
        log.info("#################################################################################################")
        log.info("Crawl-runs {} - total articles {} - total new article {} - ", crawlerStatistics.runs, crawlerStatistics.crawledArticles.size, crawlerStatistics.crawledArtilceNumber)
    }

    private fun crawl(url: String) {
        for (path: String in crawlerStatistics.urlsToCrawl[url]!!) {
            while (crawlerStatistics.lastResponseStatusCode != 404 && !crawlerStatistics.interrupt) {
                val doc: Document? = connectAndParseResponse(url, path)
                if (doc != null) {
                    val articles: Elements = doc.select("article")
                    for (article: Element in articles) {
                        if(crawlerStatistics.interrupt) {
                            return
                        }
                        if (!alreadyLoaded(article)) {
                            writeLog(article)
                            val doc2: Document = openArticle(url + article.attr("data-href"))
                            crawlerStatistics.crawledArtilceNumber++
                        }
                    }
                    crawlerStatistics.crawledSites++
                    log.info("site {} crawled - new article crawled {}", crawlerStatistics.crawledSites, crawlerStatistics.crawledArtilceNumber)
                }
            }
            if(crawlerStatistics.interrupt) {
               return
            }
        }
    }
    
    private fun connectAndParseResponse(url: String, path: String): Document? {
        val pathWithPageNumber: String = path.replace("{seitenID}", (crawlerStatistics.crawledSites + 1).toString())
        val response: Response = Jsoup.connect(url + pathWithPageNumber).execute()
        crawlerStatistics.lastResponseStatusCode = response.statusCode()
        
        if (crawlerStatistics.lastResponseStatusCode == 404) {
            log.error("status code was 404")
            crawlerStatistics.done = true
            return null
        }
        log.info("return document for {}", url + pathWithPageNumber)
        return response.parse()
    }
    
    private fun loadLog() {
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

    private fun writeLog(article: Element) {
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
    
    private fun alreadyLoaded(article: Element): Boolean {
        var alreadyLoaded = false
        val articleIid: String = article.attr("data-adid")
        val articleIUri: String = article.attr("data-href")
        if (!crawlerStatistics.crawledArticles.isEmpty() &&  crawlerStatistics.crawledArticles.containsKey(articleIid) && crawlerStatistics.crawledArticles[articleIid].equals(articleIUri)) {
            alreadyLoaded = true
            log.info("the article {} - {} was already loaded", articleIid, articleIUri)
        }
        return alreadyLoaded
    }
    
    private fun openArticle(articleUrl: String): Document {
        log.info("get article {}", articleUrl)
        return Jsoup.connect(articleUrl).get()
    }
}